/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig;

import javafx.stage.Stage;
import org.jevis.jeapi.JEVisDataSource;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ConfigManager {

    private static ConfigManager instance;
    private static Stage primaryStage;
    private static JEVisDataSource ds;

    public JEVisDataSource getDataSource() {
        return ds;
    }

    public void setDataSource(JEVisDataSource newds) {
        ConfigManager.ds = newds;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        ConfigManager.primaryStage = primaryStage;
    }

    public synchronized static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
}
