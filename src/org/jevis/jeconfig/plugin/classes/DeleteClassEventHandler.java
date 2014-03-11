/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.classes;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Dialogs;
import javafx.scene.control.TreeView;
import org.jevis.jeapi.JEVisClass;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.plugin.object.ObjectItem;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class DeleteClassEventHandler implements EventHandler {

    private ClassItem _item;
    private JEVisClass _object;
    private TreeView _tree;

    public DeleteClassEventHandler(TreeView tree, ClassItem item, JEVisClass obj) {
        _object = obj;
        _item = item;
        _tree = tree;
    }

    @Override
    public void handle(Event t) {
        try {
            Dialogs.DialogResponse response = Dialogs.showConfirmDialog(
                    JEConfig.getStage(), "Do you want to delete the Class \"" + _object.getName() + "\" ?",
                    "Delte Object", "Delete", Dialogs.DialogOptions.OK_CANCEL);
            if (response == Dialogs.DialogResponse.OK) {
                try {
                    _object.delete();
                    _item.getParent().getChildren().remove(_item);
                } catch (Exception ex) {
                    Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);
                }
            }
        } catch (Exception ex) {
        }


    }
}
