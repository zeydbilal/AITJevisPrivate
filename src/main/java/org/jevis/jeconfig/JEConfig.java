/**
 * Copyright (C) 2014 Envidatec GmbH <info@envidatec.com>
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
 */
package org.jevis.jeconfig;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
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
import org.controlsfx.dialog.Dialogs;
import org.jevis.jeapi.JEVisDataSource;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.sql.JEVisDataSourceSQL;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class JEConfig extends Application {

    private static Stage _primaryStage;
    private static File _lastFile;

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("Java version: " + System.getProperty("java.version"));

            _primaryStage = primaryStage;
            buildGUI(primaryStage);

//            PropertySheet ps = new PropertySheet();
//            PropertySheet ps = new PropertySheet(BeanPropertyUtils.getProperties(new Calendar("", Color.ROYALBLUE)));
//            Dialog dialog = new Dialog(null, "Calendar properties");
//            dialog.setContent(ps);
//            dialog.getActions().addAll(Dialog.Actions.CANCEL);
//            Button b = new Button("Add calendar");
//            dialog.show();
//            b.setOnAction(action -> dialog.show());
            //test
        } catch (Exception ex) {
//            Dialogs.showErrorDialog(primaryStage, ex.getMessage(), "Error", null, ex);
            Dialogs.create()
                    .owner(primaryStage)
                    .title("Error while starting JEConfig")
                    .showException(ex);
        }
    }

    private void buildGUI(Stage primaryStage) {
//        Application.setUserAgentStylesheet(null);
//        StyleManager.getInstance().addUserAgentStylesheet(JEConfig.getResource("/styles/main.css"));
//        StyleManager.getInstance().addUserAgentStylesheet("/styles/Styles.css");
//        Application.setUserAgentStylesheet("/styles/main.css");
//        Application.getUserAgentStylesheet().
//        
        JEVisDataSource ds = null;

        try {
            ds = new JEVisDataSourceSQL("192.168.2.55", "3306", "jevis", "jevis", "jevistest", "Sys Admin", "jevis");
            ds.connect("Sys Admin", "jevis");
//            Login login = new Login(ds);
//            login.showLoginDialog();

//            login.showLogin(false);
        } catch (JEVisException ex) {
            Logger.getLogger(JEConfig.class.getName()).log(Level.SEVERE, null, ex);
//            Dialogs.showErrorDialog(primaryStage, ex.getMessage(), "Error", null, ex);
            Dialogs.create()
                    .owner(primaryStage)
                    .title("Error while connecting to JEVis")
                    .showException(ex);
        }

//        RootPane rp = new RootPane(ds);
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

        root.getChildren().addAll(border);

        Scene scene = new Scene(root, 300, 250);
        scene.getStylesheets().add("/styles/Styles.css");

        primaryStage.getIcons().add(getImage("1393354629_Config-Tools.png"));
        primaryStage.setTitle("JEConfig");
        primaryStage.setScene(scene);
        maximize(primaryStage);
        primaryStage.show();
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

    public static Stage getStage() {
        return _primaryStage;
    }

    public static File getLastFile() {
        return _lastFile;
    }

    public static void setLastFile(File file) {
        _lastFile = file;
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
