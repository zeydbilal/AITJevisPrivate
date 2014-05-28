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
import org.jevis.application.dialog.ConfirmDialog;
import org.jevis.application.dialog.ExceptionDialog;
import org.jevis.jeconfig.JEConfig;
import static org.jevis.jeconfig.JEConfig.PROGRAMM_INFO;

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
            ConfirmDialog dia = new ConfirmDialog();
            ConfirmDialog.Response re = dia.show(JEConfig.getStage(), "Delte Object", "Delte Object", "Do you want to delete the Class \"" + _object.getName() + "\" ?");

            if (re == ConfirmDialog.Response.YES) {
                try {
                    _object.delete();
                    _item.getParent().getChildren().remove(_item);
                } catch (Exception ex) {
                    ExceptionDialog eDia = new ExceptionDialog();
                    eDia.show(JEConfig.getStage(), "Error", "Could not delete Class", ex, PROGRAMM_INFO);
                }
            }

        } catch (Exception ex) {
        }

    }
}
