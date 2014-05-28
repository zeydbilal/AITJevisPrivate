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
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.tool.ImageConverter;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ClassItem extends TreeItem<String> {

    JEVisClass _class;
    /**
     * Control if the children of this tree item has been loaded.
     */
    private boolean hasLoadedChildren = false;
    private TreeView _tree;
    private Label _label = new Label();
    private boolean _isRoot = false;
    private JEVisDataSource _ds;

    public ClassItem(JEVisDataSource ds) {
        super("JEVisClasses");
        _isRoot = true;
        _ds = ds;
    }

    public ClassItem(JEVisClass obj) {
//        super("", JEConfig.getImage("1390343812_folder-open.png", 20, 20));//TODo get icon from class
//        _label.setText(getName(obj));

//        System.out.println("Classitem: " + obj);
        _class = obj;
        addAnimation();
//        setGraphic(_label);

        HBox cell = new HBox();
        Label name = new Label(getName(obj));
        cell.getChildren().addAll(getIcon(), name);
        setGraphic(cell);
    }

    private ImageView getIcon() {
        try {
            return ImageConverter.convertToImageView(_class.getIcon(), 20, 20);
        } catch (Exception ex) {
            return JEConfig.getImage("1393615831_unknown2.png", 20, 20);
        }
    }

    private String getName(JEVisClass obj) {
        try {
            return obj.getName();
        } catch (Exception e) {
            return "*MISSING_NAME*";
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
            if (_isRoot) {
                for (JEVisClass jc : _ds.getJEVisClasses()) {
                    final ClassItem newChild = new ClassItem(jc);
                    super.getChildren().add(newChild);
                }
            } else {

                for (JEVisClass child : _class.getHeirs()) {
                    final ClassItem newChild = new ClassItem(child);
                    super.getChildren().add(newChild);
                }
            }
        } catch (JEVisException ex) {
            Logger.getLogger(ClassItem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JEVisClass getObject() {
        return _class;
    }

    private void addAnimation() {
    }

    public boolean isRoot() {
        return _isRoot;
    }
}
