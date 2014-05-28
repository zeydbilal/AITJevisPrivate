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
import org.jevis.api.JEVisObject;
import org.jevis.application.dialog.ConfirmDialog;
import org.jevis.jeconfig.JEConfig;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class DeleteObjectEventHandler implements EventHandler {

    private TreeItem<ObjectTreeObject> _item;
    private JEVisObject _object;
    private TreeView _tree;

    public DeleteObjectEventHandler(TreeView tree, TreeItem<ObjectTreeObject> item, JEVisObject obj) {
        _object = obj;
        _item = item;
        _tree = tree;
    }

    @Override
    public void handle(Event t) {

        ConfirmDialog dia = new ConfirmDialog();
        String question = "Do you want to delete the Class \"" + _object.getName() + "\" ?";

        if (dia.show(JEConfig.getStage(), "Delete Object", "Delete Object?", question) == ConfirmDialog.Response.YES) {
            try {
                System.out.println("User want to delete: " + _object.getName());
                System.out.println("Parent: " + _item.getParent());

                TreeItem<ObjectTreeObject> parent = _item.getParent();

                parent.getChildren().remove(_item);
//                parent.setExpanded(false);

                _object.delete();

                _tree.getSelectionModel().select(parent);

            } catch (Exception ex) {
                ex.printStackTrace();
//                    Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);

            }
        }

//        Action response = Dialogs.create()
//                .owner(JEConfig.getStage())
//                .title("Delte Object")
//                .masthead("Delte Object")
//                .message("Do you want to delete the Class \"" + _object.getName() + "\" ?")
//                .showConfirm();
//
//        if (response == Dialog.Actions.OK) {
//            // ... submit user input
//            try {
//                _object.delete();
//                _item.getParent().getChildren().remove(_item);
//            } catch (Exception ex) {
////                    Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);
//                Dialogs.create()
//                        .owner(JEConfig.getStage())
//                        .title("Error")
//                        .showException(ex);
//            }
//        } else {
//            // ... user cancelled, reset form to default
//        }
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
