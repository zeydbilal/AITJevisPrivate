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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import org.jevis.api.JEVisClass;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.tool.ImageConverter;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ClassContextMenu extends ContextMenu {

    private JEVisClass _obj;
    private TreeItem<JEVisClass> _item;
    private ClassTree _tree;

    public ClassContextMenu(TreeItem<JEVisClass> item, ClassTree tree) {
        super();
        System.out.println("build Contex menu");
        _obj = item.getValue();
        _item = item;
        _tree = tree;

        getItems().add(buildMenuNew());

//        getItems().add(new SeparatorMenuItem());
//        getItems().add(buildDelete());
//        getItems().add(buildRename());
//        getItems().add(buildProperties())
        getItems().setAll(buildMenuNew(), new SeparatorMenuItem(), buildDelete(), buildRename());

    }

    public Menu buildMenuNew() {
        Menu addMenu = new Menu("New", JEConfig.getImage("list-add.png", 20, 20));
        addMenu.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                _tree.fireEventNew(_tree.getSelectionModel().getSelectedItem());
            }
        });

        System.out.println("Disable menu: " + !JEConfig.getCurrentUser().isSysAdmin());
        addMenu.setDisable(!JEConfig.getCurrentUser().isSysAdmin());

        return addMenu;

    }

    private ImageView getIcon(JEVisClass jclass) {
        try {
            return ImageConverter.convertToImageView(jclass.getIcon(), 20, 20);
        } catch (Exception ex) {
            return JEConfig.getImage("1393615831_unknown2.png", 20, 20);
        }
    }

    private MenuItem buildProperties() {
        MenuItem menu = new MenuItem("Expand");//shoud be edit but i use it for expand for the time
        menu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
//                PopOver popup = new PopOver(new HBox());
//                popup.show(_item.getGraphic(), 200d, 200d, Duration.seconds(1));
                //TMP test

//                System.out.println("expand all");
//                _item.expandAll(true);
            }
        });
        return menu;
    }

    private MenuItem buildRename() {
        MenuItem menu = new MenuItem("Rename");
        menu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

//                _tree.edit(_item);
                //workaround
                if (_tree instanceof ClassTree) {
                    ((ClassTree) _tree).fireEventRename();
                }

            }
        });
        menu.setDisable(!JEConfig.getCurrentUser().isSysAdmin());

        return menu;
    }

    private MenuItem buildDelete() {
        MenuItem menu = new MenuItem("Delete", JEConfig.getImage("list-remove.png", 20, 20));
        menu.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                _tree.fireDelete(_obj);
            }
        });
        menu.setDisable(!JEConfig.getCurrentUser().isSysAdmin());
        return menu;
    }
}
