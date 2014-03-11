/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.classes;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Dialogs.DialogOptions;
import javafx.scene.control.Dialogs.DialogResponse;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.jevis.jeapi.JEVisClass;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jeconfig.JEConfig;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ClassContextMenu extends ContextMenu {

    private JEVisClass _obj;
    private ClassItem _item;
    private TreeView _tree;

    public ClassContextMenu(JEVisClass obj, ClassItem item, TreeView tree) {
        super();
        _obj = obj;
        _item = item;
        _tree = tree;

        getItems().add(buildMenuNew());
        getItems().add(new SeparatorMenuItem());
        getItems().add(buildDelete());
        getItems().add(buildRename());
        getItems().add(buildProperties());


    }

    private MenuItem buildMenuNew() {
        MenuItem addMenu = new MenuItem("New");
        addMenu.setOnAction(new NewClassEventHandler(_tree, _item, _obj));
        return addMenu;

    }

    private MenuItem buildProperties() {
        MenuItem menu = new MenuItem("Edit");
        menu.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
//                PopOver popup = new PopOver(new HBox());
//                popup.show(_item.getGraphic(), 200d, 200d, Duration.seconds(1));
            }
        });
        return menu;
    }

    private MenuItem buildRename() {
        MenuItem menu = new MenuItem("Rename");
        menu.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {

                _tree.setEditable(true);
                _tree.edit(_item);
                _tree.setEditable(false);
            }
        });
        return menu;
    }

    private MenuItem buildDelete() {
        MenuItem menu = new MenuItem("Delete");
        menu.setOnAction(new DeleteClassEventHandler(_tree, _item, _obj));
        return menu;
    }
}
