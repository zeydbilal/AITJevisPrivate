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
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com> //
 */
public class ClassItem extends TreeItem<ClassTreeObject> {

    private boolean hasLoadCh = false;

    final HBox cell = new HBox();
    ImageView icon = new ImageView();
    Label nameLabel = new Label("*Missing*");

    public ClassItem(JEVisClass obj) {
        super(new ClassTreeObject(obj));
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
                ((ClassItem) child).expandAll(expand);
            }
        }
    }

    private void initCildren() {
        hasLoadCh = true;
        try {
            for (JEVisClass child : getValue().getObject().getHeirs()) {
                final TreeItem<ClassTreeObject> childItem = new ClassItem(child);
                super.getChildren().add(childItem);

            }
        } catch (Exception ex) {
            Logger.getLogger(ClassItem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public ObservableList<TreeItem<ClassTreeObject>> getChildren() {
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

}
