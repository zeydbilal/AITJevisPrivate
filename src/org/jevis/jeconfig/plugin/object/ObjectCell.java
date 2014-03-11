/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.object;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ObjectCell extends TreeCell<String> {
    //

    private TextField textField;

    public ObjectCell() {
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
            ObjectItem objectItem = (ObjectItem) getTreeItem();
            JEVisObject object = objectItem.getObject();
            object.setName(t);
            object.commit();
            super.commitEdit(t);
        } catch (JEVisException ex) {
            Logger.getLogger(ObjectCell.class.getName()).log(Level.SEVERE, null, ex);
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
        ObjectContextMenu menu = null;

        try {
            ObjectItem objectItem = (ObjectItem) getTreeItem();
            JEVisObject object = objectItem.getObject();
            menu = new ObjectContextMenu(object, objectItem, getTreeView());

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
