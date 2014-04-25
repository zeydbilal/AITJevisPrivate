/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.object;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeView;
import org.jevis.jeapi.JEVisClass;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jeconfig.plugin.object.ObjectItem;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class NewObjectEventHandler implements EventHandler {

    private ObjectItem _item;
    private JEVisObject _object;
    private JEVisClass _class;
    private TreeView _tree;

    public NewObjectEventHandler(TreeView tree, ObjectItem item, JEVisObject obj, JEVisClass jclass) {
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
            ObjectItem newItem = new ObjectItem(newObject);
            _item.getChildren().add(newItem);
            _tree.getSelectionModel().select(newItem);

        } catch (JEVisException ex) {
            System.out.println("new object: " + ex);
        }
    }
}
