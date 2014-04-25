/**
 * Copyright (C) 2014 Envidatec GmbH <info@envidatec.com>
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
 */
package org.jevis.jeconfig.plugin.classes;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.jevis.jeapi.JEVisDataSource;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ClassTree {

    private TreeView _tree;
    ClassTreeChangeListener _cl;

    public ClassTree() {
    }

    public TreeView<JEVisObject> SimpleTreeView(JEVisDataSource ds, VBox editorPane) {
        try {

            ClassItem rootItem = new ClassItem(ds);//TDO replace with root  
            rootItem.setExpanded(true);
            TreeView treeView = new TreeView(rootItem);
            _tree = treeView;

            treeView.showRootProperty().set(false);
            treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            _cl = new ClassTreeChangeListener(editorPane);
            treeView.getSelectionModel().selectedItemProperty().addListener(_cl);
            treeView.setOnKeyReleased(new TreeHotKeys(treeView, _cl));

//            treeView.setEditable(true);
            treeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
                @Override
                public TreeCell<String> call(TreeView<String> p) {
                    return new ClassCell();
                }
            });
            treeView.setId("objecttree");
//            treeView.setStyle(JEConfig.getResource("Styles.css"));
            treeView.getStylesheets().add("/styles/Styles.css");

            return treeView;
        } catch (Exception ex) {
            Logger.getLogger(ClassTree.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    private void addChildren(TreeItem<JEVisObject> treeItemRoot, JEVisObject obj) {
        try {
            for (JEVisObject child : obj.getChildren()) {
                Image folderIcon = new Image(getClass().getResourceAsStream("1390343812_folder-open.png"));
                Image objectIcon = new Image(getClass().getResourceAsStream("1390344346_3d_objects.png"));
                TreeItem<JEVisObject> childItem;
                if (!child.getChildren().isEmpty()) {
                    childItem = new TreeItem<>(child, new ImageView(folderIcon));
                } else {
                    childItem = new TreeItem<>(child, new ImageView(objectIcon));
                }

                treeItemRoot.getChildren().add(childItem);
                if (!child.getChildren().isEmpty()) {

                    addChildren(childItem, child);
                }
            }
        } catch (JEVisException ex) {
            Logger.getLogger(ClassTree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fireEventRename() {
        _tree.setEditable(true);
        _tree.edit(_cl.getCurrentItem());
        _tree.setEditable(false);
    }

    public void fireSave() throws JEVisException {
        _cl.getCurrentEditor().comitAll();
    }

    private class TreeHotKeys implements EventHandler<KeyEvent> {

        private TreeView _tree;
        private ClassTreeChangeListener _listener;

        public TreeHotKeys(TreeView tree, ClassTreeChangeListener listener) {
            _tree = tree;
            _listener = listener;
        }

        @Override
        public void handle(KeyEvent t) {
            if (t.getCode() == KeyCode.F2) {
                fireEventRename();
            }

        }
    }
}
