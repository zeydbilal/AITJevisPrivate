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

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.jevis.api.JEVisObject;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com> //
 */
public class ObjectItem extends TreeItem<TreeObject> {

    private boolean hasLoadCh = false;

    final HBox cell = new HBox();
    ImageView icon = new ImageView();
    Label nameLabel = new Label("*Missing*");

    public ObjectItem(JEVisObject obj) {
        super(new TreeObject(obj));
//        buildGraphic();
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

    private void initCildren() {
        hasLoadCh = true;
        try {
            for (JEVisObject child : getValue().getObject().getChildren()) {
                final TreeItem<TreeObject> childItem = new ObjectItem(child);
                super.getChildren().add(childItem);

            }
        } catch (Exception ex) {
            Logger.getLogger(ObjectItem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public ObservableList<TreeItem<TreeObject>> getChildren() {
        if (!hasLoadCh) {
            initCildren();
        }
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        if (!hasLoadCh) {
            initCildren();
        }
        return getChildren().isEmpty();
    }

//    //i wonder why the Item holds the graphic and not the Cell..
//    private void buildGraphic() {
//        System.out.println("build graphic for: " + getValue().getObject().getName());
//        icon = getIcon(getValue().getObject());
//        nameLabel.setText(getValue().getObject().getName());
//
//        cell.getChildren().setAll(icon, nameLabel);
//
//        setGraphic(cell);
//
//    }
//
//    private ImageView getIcon(JEVisObject item) {
//        try {
//            if (item != null && item.getJEVisClass() != null) {
//                return ImageConverter.convertToImageView(item.getJEVisClass().getIcon(), 20, 20);
//            } else {
//                return JEConfig.getImage("1390343812_folder-open.png", 20, 20);
//            }
//
//        } catch (Exception ex) {
//            System.out.println("Error while get icon for object: " + ex);
//
//            try {
//                //Fallback icons
//                if (isLeaf()) {
//                    return JEConfig.getImage("1390344346_3d_objects.png", 20, 20);
//                } else {
//                    return JEConfig.getImage("1390343812_folder-open.png", 20, 20);
//                }
//            } catch (NullPointerException ex2) {
//
//            }
//        }
//        return new ImageView();
//
//    }
}
