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

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.application.dialog.ConfirmDialog;
import org.jevis.jeconfig.JEConfig;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class DeleteClassEventHandler implements EventHandler {

    private TreeItem<ClassTreeObject> _item;
    private JEVisClass _class;
    private TreeView _tree;

    public DeleteClassEventHandler(TreeView tree, TreeItem<ClassTreeObject> item) {
        _item = item;
        _tree = tree;
        _class = _item.getValue().getObject();
    }

    @Override
    public void handle(Event t) {

        try {
            ConfirmDialog dia = new ConfirmDialog();
            String question = "Do you want to delete the Class \"" + _class.getName() + "\" ?";

            if (dia.show(JEConfig.getStage(), "Delete Class", "Delete Class?", question) == ConfirmDialog.Response.YES) {
                try {
                    System.out.println("User want to delete: " + _class.getName());
//                    System.out.println("Parent: " + _item.getParent());

                    TreeItem<ClassTreeObject> parent = _item.getParent();

                    parent.getChildren().remove(_item);
                    _class.delete();

                    _tree.getSelectionModel().select(parent);

                } catch (Exception ex) {
                    ex.printStackTrace();
//                    Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);

                }
            }
        } catch (JEVisException ex) {
            Logger.getLogger(DeleteClassEventHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
