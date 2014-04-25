/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//import javafx.scene.control.Dialogs;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class TopMenu extends MenuBar {

    public TopMenu() {
        super();
        Menu menuFile = new Menu("File");
        menuFile.getItems().add(new MenuItem("New"));
        menuFile.getItems().add(new SeparatorMenuItem());
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                System.exit(0);
            }
        });
        menuFile.getItems().add(exit);

        // --- Menu Edit
        Menu menuEdit = new Menu("Edit");
        MenuItem copie = new MenuItem("Copie");
        MenuItem delete = new MenuItem("Delete");
        MenuItem rename = new MenuItem("Rename");
        rename.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            }
        });

        menuEdit.getItems().addAll(copie, delete, rename);

        // --- Menu View
        Menu menuView = new Menu("View");

        Menu help = new Menu("Help");
        MenuItem about = new MenuItem("About");
        help.getItems().add(about);
        about.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
//                Dialogs.showInformationDialog(JEConfig.getStage(), "JEConfig Version: 3.0.0\n\nJEAPI Version: 3.0.01", "About", "JEConfig About");
                Dialogs.create()
                        .owner(JEConfig.getStage())
                        .title("About")
                        .masthead("JEConfig About")
                        .message("JEConfig Version: 3.0.0\n\nJEAPI Version: 3.0.01")
                        .showInformation();

            }
        });

        getMenus().addAll(menuFile, menuEdit, menuView, help);
    }
}
