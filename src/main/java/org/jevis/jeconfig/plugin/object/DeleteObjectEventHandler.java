/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.object;

import javafx.event.Event;
import javafx.event.EventHandler;
//import javafx.scene.control.Dialogs;
import javafx.scene.control.TreeView;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jeconfig.JEConfig;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class DeleteObjectEventHandler implements EventHandler {

    private ObjectItem _item;
    private JEVisObject _object;
    private TreeView _tree;

    public DeleteObjectEventHandler(TreeView tree, ObjectItem item, JEVisObject obj) {
        _object = obj;
        _item = item;
        _tree = tree;
    }

    @Override
    public void handle(Event t) {

        Action response = Dialogs.create()
                .owner(JEConfig.getStage())
                .title("Delte Object")
                .masthead("Delte Object")
                .message("Do you want to delete the Class \"" + _object.getName() + "\" ?")
                .showConfirm();

        if (response == Dialog.Actions.OK) {
            // ... submit user input
            try {
                _object.delete();
                _item.getParent().getChildren().remove(_item);
            } catch (Exception ex) {
//                    Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);
                Dialogs.create()
                        .owner(JEConfig.getStage())
                        .title("Error")
                        .showException(ex);
            }
        } else {
            // ... user cancelled, reset form to default
        }

//        Dialogs.DialogResponse response = Dialogs.showConfirmDialog(
//                JEConfig.getStage(), "Do you want to delete the object [" + _object.getID() + "] \"" + _object.getName() + "\" ?",
//                "Delte Object", "Delete", Dialogs.DialogOptions.OK_CANCEL);
//        if (response == Dialogs.DialogResponse.OK) {
//            try {
//                _object.delete();
//                _item.getParent().getChildren().remove(_item);
//            } catch (Exception ex) {
////                Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);
//                Dialogs.create()
//                        .owner(JEConfig.getStage())
//                        .title("Error")
//                        .showException(ex);
//            }
//        }
    }
}
