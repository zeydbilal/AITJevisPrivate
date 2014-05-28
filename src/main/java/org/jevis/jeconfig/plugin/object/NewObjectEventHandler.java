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
package org.jevis.jeconfig.plugin.object;

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
public class NewObjectEventHandler implements EventHandler {

    private TreeItem<TreeObject> _item;
    private JEVisObject _object;
    private JEVisClass _class;
    private TreeView _tree;

    public NewObjectEventHandler(TreeView tree, TreeItem<TreeObject> item, JEVisObject obj, JEVisClass jclass) {
        _object = obj;
        _class = jclass;
        _item = item;
        _tree = tree;
    }

    @Override
    public void handle(Event t) {
        try {
            JEVisObject newObject = _object.buildObject("New " + _class.getName(), _class);
            newObject.commit();
            ObjectItem treeItem = new ObjectItem(newObject);
            _item.getChildren().add(treeItem);
            _tree.getSelectionModel().select(treeItem);

        } catch (JEVisException ex) {
            System.out.println("new object: " + ex);
        }
    }
}
