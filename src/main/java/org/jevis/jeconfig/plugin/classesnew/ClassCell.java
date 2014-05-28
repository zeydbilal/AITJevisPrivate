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
package org.jevis.jeconfig.plugin.classesnew;

import org.jevis.jeconfig.plugin.object.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import org.jevis.api.JEVisException;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ClassCell extends TreeCell<ClassTreeObject> {

    private TextField textField;

    @Override
    public void startEdit() {
//        super.startEdit();//made some problems with tree.setEdibale(false)
        setGraphic(getTreeItem().getValue().getEditor(this));

    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        try {
            getTreeItem().getValue().getObject().rollBack();
        } catch (JEVisException ex) {
            Logger.getLogger(ClassCell.class.getName()).log(Level.SEVERE, null, ex);
        }

        setGraphic(getTreeItem().getValue().getGraphic());
    }

    @Override
    public void commitEdit(ClassTreeObject t) {
        super.commitEdit(t);
        updateItem(t, false);

    }

    @Override
    public void updateItem(ClassTreeObject item, boolean emty) {
        super.updateItem(item, emty);

        if (!emty) {
            setGraphic(getTreeItem().getValue().getGraphic());
            addContexMenu(getTreeItem());

        }

    }

    private void addContexMenu(TreeItem<ClassTreeObject> objectItem) {
        try {
            ClassContextMenu menu = new ClassContextMenu(objectItem, getTreeView());
            setContextMenu(menu);

        } catch (Exception ex) {
        }
    }

}
