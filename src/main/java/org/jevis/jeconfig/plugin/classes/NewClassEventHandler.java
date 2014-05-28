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
import javafx.scene.control.TreeView;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisConstants;
import org.jevis.api.JEVisException;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class NewClassEventHandler implements EventHandler {

    private ClassItem _item;
    private JEVisClass _object;
    private TreeView _tree;

    public NewClassEventHandler(TreeView tree, ClassItem item, JEVisClass obj) {
        _object = obj;
        _item = item;
        _tree = tree;
    }

    @Override
    public void handle(Event t) {
        try {
            JEVisClass newClass = _object.getDataSource().buildClass("New Class");
            if (!_item.isRoot()) {
                newClass.buildRelationship(_object, JEVisConstants.ClassRelationship.INHERIT, JEVisConstants.Direction.FORWARD);
                newClass.commit();
            }

            ClassItem newItem = new ClassItem(newClass);
            _item.getChildren().add(newItem);
            _tree.getSelectionModel().select(newItem);

        } catch (JEVisException ex) {
            System.out.println("new object: " + ex);
        }
    }
}
