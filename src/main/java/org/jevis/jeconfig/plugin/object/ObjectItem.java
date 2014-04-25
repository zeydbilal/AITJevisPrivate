/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.object;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.tool.ImageConverter;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com> //
 */
public class ObjectItem extends TreeItem<String> {

    JEVisObject _object;
    /**
     * Control if the children of this tree item has been loaded.
     */
    private boolean hasLoadedChildren = false;
    private TreeView _tree;

    public ObjectItem(JEVisObject obj) {
//        super(obj.getName(), JEConfig.getImage("1390343812_folder-open.png", 20, 20));//TODo get icon from class
        _object = obj;
        addAnimation();

        HBox cell = new HBox();
        Label name = new Label(obj.getName());
        cell.getChildren().addAll(getIcon(obj), name);

        setGraphic(cell);
    }

    private ImageView getIcon(JEVisObject obj) {
        try {
//            System.out.println("obj-class: " + obj.getJEVisClass().getName());
//            throw new RuntimeException();

            return ImageConverter.convertToImageView(_object.getJEVisClass().getIcon(), 20, 20);

        } catch (Exception ex) {
            System.out.println("Error while get icon for object: " + ex);
            if (isLeaf()) {
                return JEConfig.getImage("1390344346_3d_objects.png", 20, 20);
            } else {
                return JEConfig.getImage("1390343812_folder-open.png", 20, 20);
            }
        }

    }

    public void expandAll(boolean expand) {
        if (!isLeaf()) {
            if (isExpanded() && !expand) {
                setExpanded(expand);
            } else if (!isExpanded() && expand) {
                setExpanded(expand);
            }

            for (TreeItem child : getChildren()) {
                ((ObjectItem) child).expandAll(expand);
            }
        }
    }

    @Override
    public ObservableList<TreeItem<String>> getChildren() {
        if (hasLoadedChildren == false) {
            loadChildren();
        }
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        if (hasLoadedChildren == false) {
            loadChildren();
        }
        return super.getChildren().isEmpty();
    }

    /**
     * Create some dummy children for this item.
     */
    private void loadChildren() {
        hasLoadedChildren = true;
        try {
            for (JEVisObject child : _object.getChildren()) {
                final ObjectItem newChild = new ObjectItem(child);
                super.getChildren().add(newChild);

            }
        } catch (JEVisException ex) {
            Logger.getLogger(ObjectItem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JEVisObject getObject() {
        return _object;
    }

    private void addAnimation() {
    }
}
