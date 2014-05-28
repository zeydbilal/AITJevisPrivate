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

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.plugin.classesnew.editor.ClassEditor;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ClassTree extends TreeView<ClassTreeObject> {

    private TreeView _tree;
    private ClassTreeChangeListener _cl;
    private ClassEditor _editor = new ClassEditor();
    private boolean _editable = false;

    public ClassTree() {

    }

    public TreeView getTreeView() {
        return _tree;
    }

    public ClassTree(JEVisDataSource ds) {
        super();
        try {

            JEVisClass root = new JEVisRootClass(ds);
            TreeItem<ClassTreeObject> rootItem = new ClassItem(root);

            setShowRoot(false);

            getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            _cl = new ClassTreeChangeListener(_editor);

            getSelectionModel().selectedItemProperty().addListener(_cl);
//            setOnKeyReleased(new TreeHotKeys(_tree, _cl));

            setCellFactory(new Callback<TreeView<ClassTreeObject>, TreeCell<ClassTreeObject>>() {
                @Override
                public TreeCell<ClassTreeObject> call(TreeView<ClassTreeObject> p) {
                    return new ClassCell();
                }
            });

            addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

                @Override
                public void handle(KeyEvent t) {
                    if (t.getCode() == KeyCode.F2) {
                        System.out.println("F2 rename event");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                fireEventRename();
                            }
                        });

                    } else if (t.getCode() == KeyCode.DELETE) {
                        fireDelete();
                    }
                }

            });

            setId("objecttree");

            getStylesheets().add("/styles/Styles.css");
            setPrefWidth(500);

            setRoot(rootItem);
            setEditable(true);

        } catch (Exception ex) {
//            Logger.getLogger(ObjectTree.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

    }

    /**
     * Workaround, it was not posible to have an double click without the chnage
     * for the default edit. Its posible to ste setEditable(false) but then we
     * had strange behavior.
     *
     * @param ti
     */
    @Override
    public void edit(TreeItem<ClassTreeObject> ti) {
        if (_editable) {
//            System.out.println("edit allowed");
            editableProperty().setValue(true);
            super.edit(ti);
            _editable = false;
        } else {
//            System.out.println("not allowed");
        }
    }

    public void expandSelected(boolean expand) {
        TreeItem<ClassTreeObject> item = _cl.getCurrentItem();
        expand(item, expand);
    }

    private void expand(TreeItem<ClassTreeObject> item, boolean expand) {
        if (!item.isLeaf()) {
            if (item.isExpanded() && !expand) {
                item.setExpanded(expand);
            } else if (!item.isExpanded() && expand) {
                item.setExpanded(expand);
            }

            for (TreeItem<ClassTreeObject> child : item.getChildren()) {
                expand(child, expand);
            }
        }
    }

    public synchronized void setEditFix(boolean edit) {
        System.out.println("Fix allow: " + edit);
        _editable = edit;
    }

    public void fireEventRename() {
        System.out.println("fireRename");
        setEditFix(true);
        edit(_cl.getCurrentItem());
    }

    public void fireSaveAttributes(boolean ask) throws JEVisException {

//        if (ask) {
//            _editor.checkIfSaved(null);
//        } else {
//            _editor.commitAll();
//        }
    }

    public void fireDelete() {
//        DeleteObjectEventHandler deletEvent = new DeleteObjectEventHandler(
//                this, _cl.getCurrentItem(), _cl.getCurrentItem().getValue().getObject());
//        deletEvent.handle(null);
    }

    public void fireEventNew() {
        ClassContextMenu newMenu = new ClassContextMenu(_cl.getCurrentItem(), _tree);

        newMenu.getItems().clear();
        newMenu.getItems().addAll(newMenu.buildMenuNew());

        com.sun.glass.ui.Robot robot
                = com.sun.glass.ui.Application.GetApplication().createRobot();

        int x = robot.getMouseX();
        int y = robot.getMouseY();
        newMenu.show(JEConfig.getStage(), x, y);
    }

    //TODO i dont like this way
    public ClassEditor getEditor() {
        return _editor;
    }
}
