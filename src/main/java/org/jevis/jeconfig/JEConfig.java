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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.measure.quantity.Dimensionless;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisRelationship;
import org.jevis.api.JEVisType;
import org.jevis.api.JEVisUnit;
import org.jevis.application.application.JavaVersionCheck;
import org.jevis.application.dialog.ExceptionDialog;
import org.jevis.application.dialog.LoginDialog;
import org.jevis.application.statusbar.Statusbar;
import org.jevis.commons.application.ApplicationInfo;
import org.jevis.commons.json.JsonUnit;
import org.jevis.commons.unit.JEVisUnitImp;
import org.jevis.jeconfig.plugin.object.attribute.AttributeSettingsDialog;
import org.jevis.jeconfig.tool.WelcomePage;

/**
 * This is the main class of the JEConfig. The JEConfig is an JAVAFX programm,
 * the early version will need the MAVEN javafx 2.0 plugin to be build for java
 * 1.7
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class JEConfig extends Application {

    final Configuration _config = new Configuration();

    private static Stage _primaryStage;
    private static File _lastFile;
    private static JEVisDataSource _mainDS;

    JEVisDataSource ds = null;

    /**
     * Defines the version information in the about dialog
     */
    public static ApplicationInfo PROGRAMM_INFO = new ApplicationInfo("JEConfig", "3.0.7 2015-01-30");
    private static Preferences pref = Preferences.userRoot().node("JEVis.JEConfig");
    private static String _lastpath = "";

    @Override
    public void init() throws Exception {
        super.init(); //To change body of generated methods, choose Tools | Templates.
//        System.out.println("Codebase: " + getHostServices().getCodeBase());
//        System.out.println("getDocumentBase: " + getHostServices().getDocumentBase());
        Parameters parameters = getParameters();

        _config.parseParameters(parameters);

    }

    @Override
    public void start(Stage primaryStage) {

//        System.out.println("edbug");
//        InfoDialog debug = new InfoDialog();
//        debug.show(primaryStage, "Debug", "Debug Info", _config.getLoginIcon() + " \n"); //        System.out.println("Java version: " + System.getProperty("java.version"));
        //does this even work on an JAVA FX Application?
        JavaVersionCheck checkVersion = new JavaVersionCheck();
        if (!checkVersion.isVersionOK()) {
            System.exit(1);
        }
        for (Map.Entry<String, String> entry : getParameters().getNamed().entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }

        _primaryStage = primaryStage;
        buildGUI(primaryStage);

    }

    /**
     * Build an new JEConfig Login and main frame/stage
     *
     * @param primaryStage
     */
    private void buildGUI(Stage primaryStage) {

        try {

            LoginDialog loginD = new LoginDialog();
//            ds = loginD.showSQL(primaryStage, _config.getLoginIcon());

            ds = loginD.showSQL(primaryStage);//Default
//            ds = loginD.showSQL(primaryStage, _config.getLoginIcon(), _config.getEnabledSSL(), _config.getShowServer(), _config.getDefaultServer());

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

//        try {
//            JEVisClassPackageManager cpm = new JEVisClassPackageManager("/tmp/SystemExport.jar", ds);
//            cpm.setContent(ds.getJEVisClasses());
//            cpm.importIntoJEVis(ds);
////            cpm.addJEVisClass(ds.getJEVisClass("Data"));
//        } catch (Exception ex) {
//            System.out.println("error while testing class export:");
//            ex.printStackTrace();
//        }
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
            //            WelcomePage welcome = new WelcomePage(primaryStage, new URI("http://coffee-project.eu/"));
//            WelcomePage welcome = new WelcomePage(primaryStage, new URI("http://openjevis.org/projects/openjevis/wiki/JEConfig3#JEConfig-Version-3"));
            WelcomePage welcome = new WelcomePage(primaryStage, _config.getWelcomeURL());

        } catch (URISyntaxException ex) {
            Logger.getLogger(JEConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
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

    }

    private void printTree(JEVisObject obj) {

        try {
            System.out.println("-Obj: " + obj);
            for (JEVisRelationship rel : obj.getRelationships()) {
                System.out.println("----Rel: " + rel);
            }
        } catch (JEVisException ex) {
            Logger.getLogger(JEConfig.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * just for testing, can be deleted
     *
     * @throws Exception
     */
    private void doTest() throws Exception {

        Unit kwh = SI.KILO(SI.WATT).times(NonSI.HOUR);

        //        System.out.println("u1: " + UnitFormat.getInstance().format(NonSI.TON_UK));
//        System.out.println("u2: " + NonSI.TON_US.getDimension());
//        System.out.println("u3: " + SI.WATT.times(NonSI.HOUR));
//        Unit kwh = SI.KILO(SI.WATT).times(NonSI.HOUR);
//        System.out.println("kwh.tostring: " + kwh);
//        System.out.println("D1: " + SI.KILO(SI.WATT.times(NonSI.HOUR)));
//        Unit parsedUnit = Unit.valueOf("(W·s)");
//        System.out.println("pu: " + parsedUnit);
//        System.out.println("pu1: " + parsedUnit.getDimension());
//        System.out.println("hash: " + parsedUnit.hashCode());
//        System.out.println("Yaher: " + NonSI.YEAR);
//        System.out.println("kw=: " + ProductUnit.valueOf(SI.KILO(SI.WATT).toString()));
////        System.out.println("hash1: " + SI.KILO(SI.WATT.times(SI.SECOND)).hashCode());
//
//        Unit komplex = SI.WATT.times(NonSI.HOUR).divide(SI.KILOGRAM).times(SI.HERTZ);
//        System.out.println("komplex: " + komplex);
//        System.out.println("komplex.equal: " + komplex.equals(ProductUnit.valueOf(komplex.toString())));
//        System.out.println("Pars kwh: " + kwh.equals(ProductUnit.valueOf(kwh.toString())));
//        System.out.println("kwh.standart: " + kwh.getStandardUnit());
//        Measurable kwh100 = Measure.valueOf(100, kwh);
//        System.out.println("in mega: " + kwh100.doubleValue(SI.MEGA(SI.WATT).times(NonSI.HOUR)));
//        System.out.println("dim1: " + kwh.pow(10));
//        System.out.println("dim2: " + kwh.root(10));
//        System.out.println("hmmm: " + kwh.divide(kwh.getStandardUnit()));
//        System.out.println("hmmm: " + kwh.divide(kwh.getStandardUnit()).getDimension());
//
//        System.out.println("asdasdasdasd: " + kwh100.doubleValue(SI.MEGA(kwh.getStandardUnit())));
//
//        Unit proU = ProductUnit.valueOf(SI.KILO((SI.WATT.times(NonSI.HOUR))).toString());
////(w-s)*3600
//        System.out.println("??: " + ProductUnit.valueOf((SI.WATT.times(NonSI.HOUR)).toString()));
//
//        Unit newU = SI.GRAM.divide(SI.MILLI(NonSI.LITER));
//        System.out.println("ml = " + newU);
//        System.out.println("gollom: " + kwh.divide(1000));
//
//        System.out.println(" eq: " + newU.equals(ProductUnit.valueOf(newU.toString())));
//        System.out.println("iiii: " + kwh.getDimension());
//
//        Unit gmol = SI.GRAM.divide(SI.MOLE);
//        gmol.getDimension();
//        gmol.getStandardUnit();
//
//        System.out.println("s-unit: " + gmol.isStandardUnit());
//        Unit algmol = gmol.alternate("CO2");
//
//        System.out.println("CO2: " + algmol);
//
//        Unit co2kwh = gmol.divide(kwh);
//        System.out.println("co2/mol= " + co2kwh);
//
//        Unit km100 = SI.KILOMETER.times(100);
//        System.out.println("kWh100: " + km100);
//
//        UnitFormat uf = UnitFormat.getInstance();
//        uf.label(km100, "100km");
//
////        Unit prettykm100 = km100.alternate("100km");
//        System.out.println("alt: " + uf.format(km100));
//        System.out.println("liter/100km: " + NonSI.LITER.divide(km100));
//
//        Unit cars = Unit.ONE.alternate("Car");
//        System.out.println("km: " + SI.KILOMETER);
//
//        Unit carKennzahl = kwh.divide(cars);
//        System.out.println("Kennzahl: " + carKennzahl);
//
////        UnitFormat uf = UnitFormat.getUCUMInstance();
////        System.out.println("hashUnitP: " + Unit.valueOf("1148846282"));
//        System.out.println("equal: " + SI.KILO(SI.WATT.times(NonSI.HOUR)).equals(proU));
//        Unit sileU = Unit.valueOf((SI.WATT.times(NonSI.HOUR)).toString());
//        System.out.println("equal2: " + SI.WATT.times(NonSI.HOUR).equals(sileU));
//        Measurable<Power> watt = Measure.valueOf(100, SI.WATT); // Ok.
//
//        watt.doubleValue(SI.WATT.times(SI.SECOND));
//
//        UnitConverter uc = SI.KILO(SI.WATT.times(NonSI.HOUR)).getConverterTo(SI.WATT);
//        System.out.println("convert: " + uc.convert(100));
        ///-------------------------------------------------
        JEVisClass food = _mainDS.getJEVisClass("Food");
        System.out.println("Class: " + food.getName());

        for (JEVisType types : food.getTypes()) {
            System.out.println("Type: " + types.getName());
        }
        for (JEVisClass vclass : food.getValidParents()) {
            System.out.println("ValidParent: " + vclass.getName());
        }

        System.out.println("");

        for (JEVisClass heir : food.getHeirs()) {
            System.out.println("-Heir: " + heir.getName());

            for (JEVisType types : heir.getTypes()) {
                System.out.println("-type: " + types.getName());
            }
            for (JEVisClass vclass : food.getValidParents()) {
                System.out.println("-ValidParent: " + vclass.getName());
            }
            System.out.println("");
        }

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
        System.out.println("main: " + args.length);
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

    /**
     * Returns the last path the local user selected
     *
     * @return
     */
    public static File getLastPath() {
        if (_lastpath.equals("")) {
            _lastpath = pref.get("lastPath", System.getProperty("user.home"));
        }
        File file = new File(_lastpath);
        if (file.exists()) {
            if (file.isDirectory()) {
                return file;
            } else {
                return file.getParentFile();
            }

        } else {
            return new File(pref.get("lastPath", System.getProperty("user.home")));
        }
    }

    /**
     * Set the last path the user selected for an file opration
     *
     * @param file
     */
    public static void setLastPath(File file) {
        _lastFile = file;
        _lastpath = file.getPath();
        pref.put("lastPath", file.getPath());

    }

    /**
     * maximized the given stage
     *
     * @param primaryStage
     */
    public static void maximize(Stage primaryStage) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
    }

    /**
     * Return an common resource
     *
     * @param file
     * @return
     */
    public static String getResource(String file) {
        //        scene.getStylesheets().addAll(this.getClass().getResource("/org/jevis/jeconfig/css/main.css").toExternalForm());

//        System.out.println("get Resouce: " + file);
        return JEConfig.class.getResource("/styles/" + file).toExternalForm();
//        return JEConfig.class.getResource("/org/jevis/jeconfig/css/" + file).toExternalForm();

    }

    /**
     * Fet an image out of the common resources
     *
     * @param icon
     * @return
     */
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

    /**
     * Get an imge in the given size from the common
     *
     * @param icon
     * @param height
     * @param width
     * @return
     */
    public static ImageView getImage(String icon, double height, double width) {
        ImageView image = new ImageView(JEConfig.getImage(icon));
        image.fitHeightProperty().set(height);
        image.fitWidthProperty().set(width);
        return image;
    }

    private void loadConfiguration(String url) {
        try {
            XMLConfiguration config = new XMLConfiguration(url);
            config.getString("webservice.port");
        } catch (ConfigurationException ex) {
            Logger.getLogger(JEConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Inform the user the some precess is working
     *
     * @param working
     */
    public static void loadNotification(final boolean working) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (working) {
                    getStage().getScene().setCursor(Cursor.WAIT);
                } else {
                    getStage().getScene().setCursor(Cursor.DEFAULT);
                }
            }
        });

    }

}
