/**
 * Copyright (C) 2009 - 2014 Envidatec GmbH <info@envidatec.com>
 *
 * This file is part of JEConfig.
 *
 * JEConfig is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation in version 3.
 *
 * JEConfig is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * JEConfig. If not, see <http://www.gnu.org/licenses/>.
 *
 * JEConfig is part of the OpenJEVis project, further project information are
 * published at <http://www.OpenJEVis.org/>.
 */
package org.jevis.jeconfig;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
//import javafx.scene.control.Dialogs;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.application.application.JavaVersionCheck;
import org.jevis.application.dialog.ExceptionDialog;
import org.jevis.application.dialog.LoginDialog;
import org.jevis.application.statusbar.Statusbar;
import org.jevis.commons.application.ApplicationInfo;
import org.jevis.jeconfig.tool.WelcomePage;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class JEConfig extends Application {

    private static Stage _primaryStage;
    private static File _lastFile;
    private static JEVisDataSource _mainDS;

    JEVisDataSource ds = null;

    public static ApplicationInfo PROGRAMM_INFO = new ApplicationInfo("JEConfig", "3.0.0 2014-07-03");
    private static Preferences pref = Preferences.userRoot().node("JEVis.JEConfig");
    private static String _lastpath = "";

    @Override
    public void start(Stage primaryStage) {
//        System.out.println("Java version: " + System.getProperty("java.version"));
        //does this even work on an JAVA FX Application?
        JavaVersionCheck ceckVersion = new JavaVersionCheck();
        if (!ceckVersion.isVersionOK()) {
            System.exit(1);
        }

        _primaryStage = primaryStage;
        buildGUI(primaryStage);

    }

    private void buildGUI(Stage primaryStage) {

        try {

            LoginDialog loginD = new LoginDialog();
            ds = loginD.showSQL(primaryStage);

            if (ds == null) {
                System.exit(0);
            }

//            ds = new JEVisDataSourceSQL("192.168.2.55", "3306", "jevis", "jevis", "jevistest", "Sys Admin", "jevis");
//            ds.connect("Sys Admin", "jevis");
        } catch (Exception ex) {
            Logger.getLogger(JEConfig.class.getName()).log(Level.SEVERE, null, ex);
            ExceptionDialog dia = new ExceptionDialog();
            dia.show(primaryStage, "Error", "Could not connect to Server", ex, PROGRAMM_INFO);

        }
        _mainDS = ds;

        JEConfig.PROGRAMM_INFO.setJEVisAPI(ds.getInfo());
        JEConfig.PROGRAMM_INFO.addLibrary(org.jevis.commons.application.Info.INFO);
        JEConfig.PROGRAMM_INFO.addLibrary(org.jevis.application.Info.INFO);

        PluginManager pMan = new PluginManager(ds);
        GlobalToolBar toolbar = new GlobalToolBar(pMan);
        pMan.addPluginsByUserSetting(null);

        StackPane root = new StackPane();
        root.setId("mainpane");

        BorderPane border = new BorderPane();
        VBox vbox = new VBox();
        vbox.getChildren().addAll(new TopMenu(), toolbar.ToolBarFactory());
        border.setTop(vbox);
        border.setCenter(pMan.getView());

        Statusbar statusBar = new Statusbar(ds);

        border.setBottom(statusBar);

        root.getChildren().addAll(border);

        Scene scene = new Scene(root, 300, 250);
        scene.getStylesheets().add("/styles/Styles.css");

        primaryStage.getIcons().add(getImage("1393354629_Config-Tools.png"));
        primaryStage.setTitle("JEConfig");
        primaryStage.setScene(scene);
        maximize(primaryStage);
        primaryStage.show();

        try {
            WelcomePage welcome = new WelcomePage(primaryStage, new URI("http://openjevis.org/projects/openjevis/wiki/JEConfig3#JEConfig-Version-3"));
        } catch (URISyntaxException ex) {
            Logger.getLogger(JEConfig.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Disable GUI is StatusBar note an disconnect
        root.disableProperty().bind(statusBar.connectedProperty.not());

        primaryStage.onCloseRequestProperty().addListener(new ChangeListener<EventHandler<WindowEvent>>() {

            @Override
            public void changed(ObservableValue<? extends EventHandler<WindowEvent>> ov, EventHandler<WindowEvent> t, EventHandler<WindowEvent> t1) {
                try {
                    System.out.println("Disconnect");
                    ds.disconnect();
                } catch (JEVisException ex) {
                    Logger.getLogger(JEConfig.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        //test
//        CSVImportDialog impDia = new CSVImportDialog();
//        impDia.show(JEConfig._primaryStage, ds);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Returns the main JEVis Datasource of this JEConfig Try not to use this
     * because it will may disapear
     *
     * @return
     * @deprecated
     */
    public static JEVisDataSource getDataSource() {
        return _mainDS;
    }

    public static Stage getStage() {
        return _primaryStage;
    }

//    public static File getLastFile() {
//        return _lastFile;
//    }
    public static File getLastPath() {
        if (_lastpath.equals("")) {
            _lastpath = pref.get("lastPath", System.getProperty("user.home"));
        }
        return new File(_lastpath);
    }

    public static void setLastPath(File file) {
        _lastFile = file;
        _lastpath = file.getPath();
        pref.put("lastPath", file.getPath());

    }

    public static void maximize(Stage primaryStage) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
    }

    public static String getResource(String file) {
        //        scene.getStylesheets().addAll(this.getClass().getResource("/org/jevis/jeconfig/css/main.css").toExternalForm());

        System.out.println("get Resouce: " + file);
        return JEConfig.class.getResource("/styles/" + file).toExternalForm();
//        return JEConfig.class.getResource("/org/jevis/jeconfig/css/" + file).toExternalForm();

    }

    public static Image getImage(String icon) {
        try {
//            System.out.println("getIcon: " + icon);
            return new Image(JEConfig.class.getResourceAsStream("/icons/" + icon));
//            return new Image(JEConfig.class.getResourceAsStream("/org/jevis/jeconfig/image/" + icon));
        } catch (Exception ex) {
            System.out.println("Could not load icon: " + "/icons/   " + icon);
            return new Image(JEConfig.class.getResourceAsStream("/icons/1393355905_image-missing.png"));
        }
    }

    public static ImageView getImage(String icon, double height, double width) {
        ImageView image = new ImageView(JEConfig.getImage(icon));
        image.fitHeightProperty().set(height);
        image.fitWidthProperty().set(width);
        return image;
    }

}
