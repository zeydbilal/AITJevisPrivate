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
package org.jevis.jeconfig.plugin.classes;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
//import javafx.scene.control.Dialogs;
//import javafx.scene.control.Dialogs.DialogOptions;
//import javafx.scene.control.Dialogs.DialogResponse;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeView;
import org.jevis.api.JEVisClass;

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
//
//        getItems().add(buildMenuNew());
//        getItems().add(new SeparatorMenuItem());
//        getItems().add(buildDelete());
//        getItems().add(buildRename());
//        getItems().add(buildProperties());
//        
        getItems().setAll(buildMenuNew(), new SeparatorMenuItem(), buildDelete(), buildRename(), buildProperties());

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
