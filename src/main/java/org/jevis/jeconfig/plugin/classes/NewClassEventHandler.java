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

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class NewClassEventHandler implements EventHandler {

    private TreeItem<ClassTreeObject> _item;
    private JEVisObject _object;
    private JEVisClass _class;
    private TreeView _tree;

    public NewClassEventHandler(TreeView tree, TreeItem<ClassTreeObject> item) {
        _item = item;
        _tree = tree;
    }

    @Override
    public void handle(Event t) {
        try {
            //TOD= ceck if class allready exists?!
            JEVisClass newClass = _object.getDataSource().buildClass("New Classs");
            newClass.commit();
            ClassItem treeItem = new ClassItem(newClass);

            //TODO: if class is a herrit from an other add it there
            _tree.getRoot().getChildren().add(treeItem);
//            _item.getChildren().add(treeItem);
            _tree.getSelectionModel().select(treeItem);

        } catch (JEVisException ex) {
            System.out.println("new object: " + ex);
        }
    }
}
