/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.classes;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeView;
import org.jevis.jeapi.JEVisClass;
import org.jevis.jeapi.JEVisConstants;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jeconfig.plugin.object.ObjectItem;

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
