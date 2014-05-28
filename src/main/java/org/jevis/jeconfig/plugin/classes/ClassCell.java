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
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ClassCell extends TreeCell<String> {
    //

    private TextField textField;

    public ClassCell() {
//        setStyle("-fx-background-color: rgba(0, 100, 100, 0);" + LABEL_STYLE);
//        setStyle(JEConfig.getResource("main.css"));
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (textField == null) {
            createTextField();
        }
        setText(null);
        setGraphic(textField);
        textField.selectAll();
    }

    @Override
    public void commitEdit(String t) {
        try {
            ClassItem objectItem = (ClassItem) getTreeItem();
            JEVisClass object = objectItem.getObject();
            object.setName(t);
            object.commit();
            super.commitEdit(t);
        } catch (JEVisException ex) {
            Logger.getLogger(ClassCell.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        try {
            setText((String) getItem());
            setGraphic(getTreeItem().getGraphic());
        } catch (Exception ex) {
        }
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        ClassContextMenu menu = null;

        try {
            ClassItem objectItem = (ClassItem) getTreeItem();
            JEVisClass object = objectItem.getObject();
            menu = new ClassContextMenu(object, objectItem, getTreeView());

        } catch (Exception ex) {
        }

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(getTreeItem().getGraphic());
//                if (!getTreeItem().isLeaf() && getTreeItem().getParent() != null) {
                setContextMenu(menu);
//                }
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(textField.getText());
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            }
        });

    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}
