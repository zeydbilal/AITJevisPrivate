/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.object;

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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.jevis.jeapi.JEVisDataSource;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jeconfig.JEConfig;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ObjectTree {

    private TreeView _tree;
    private ObjectTreeChangeListener _cl;
    private ObjectEditor _editor = new ObjectEditor();

    public ObjectTree() {
    }

    public TreeView<JEVisObject> SimpleTreeView(JEVisDataSource ds, VBox editorPane) {
        try {
            ObjectItem rootItem = new ObjectItem(ds.getObject(1l));//TDO replace with root  
            rootItem.setExpanded(true);
            TreeView treeView = new TreeView(rootItem);
            _tree = treeView;
            treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//            _cl = new ObjectTreeChangeListener(editorPane);
            _cl = new ObjectTreeChangeListener(editorPane, _editor);

            treeView.getSelectionModel().selectedItemProperty().addListener(_cl);
            treeView.setOnKeyReleased(new TreeHotKeys(treeView, _cl));

//            treeView.setEditable(true);
            treeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
                @Override
                public TreeCell<String> call(TreeView<String> p) {
                    return new ObjectCell();
                }
            });
            treeView.setId("objecttree");
            treeView.setStyle(JEConfig.getResource("main.css"));

            return treeView;
        } catch (Exception ex) {
            Logger.getLogger(ObjectTree.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public void expandSelected(boolean expand) {
        ObjectItem item = _cl.getCurrentItem();
        expand(item, expand);
    }

    private void expand(ObjectItem item, boolean expand) {
        if (!item.isLeaf()) {
            if (item.isExpanded() && !expand) {
                item.setExpanded(expand);
            } else if (!item.isExpanded() && expand) {
                item.setExpanded(expand);
            }

            for (TreeItem child : item.getChildren()) {
                expand((ObjectItem) child, expand);
            }
        }
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
            Logger.getLogger(ObjectTree.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fireEventRename() {
        _tree.setEditable(true);
        _tree.edit(_cl.getCurrentItem());
        _tree.setEditable(false);
    }

    public void fireSaveAttributes(boolean ask) throws JEVisException {

        if (ask) {
            _editor.checkIfSaved(null);
        } else {
            _editor.commitAll();
        }

    }

    public void fireDelete() {
        DeleteObjectEventHandler deletEvent = new DeleteObjectEventHandler(
                _tree, _cl.getCurrentItem(), _cl.getCurrentItem().getObject());
        deletEvent.handle(null);
    }

    public void fireEventNew() {
        ObjectContextMenu newMenu = new ObjectContextMenu(
                _cl.getCurrentItem().getObject(), _cl.getCurrentItem(), _tree);

        newMenu.getItems().clear();
        newMenu.getItems().addAll(newMenu.buildMenuNewContent());


        com.sun.glass.ui.Robot robot =
                com.sun.glass.ui.Application.GetApplication().createRobot();

        int x = robot.getMouseX();
        int y = robot.getMouseY();
        newMenu.show(JEConfig.getStage(), x, y);
    }

    private class TreeHotKeys implements EventHandler<KeyEvent> {

        private TreeView _tree;
        private ObjectTreeChangeListener _listener;

        public TreeHotKeys(TreeView tree, ObjectTreeChangeListener listener) {
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

    //TODO i dont like this way
    public ObjectEditor getEditor() {
        return _editor;
    }
}
