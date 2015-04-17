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

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.jevis.api.JEVisClass;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com> //
 */
public class ClassItem extends TreeItem<JEVisClass> {

    private boolean _doInit = true;
    private final ClassTree _tree;

    public ClassItem(JEVisClass obj) {
        super(obj);
        _tree = null;
    }

    public ClassItem(JEVisClass obj, ClassTree tree) {
        super(obj);
        _tree = tree;
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

    @Override
    public ObservableList<TreeItem<JEVisClass>> getChildren() {
        if (_doInit) {
            _doInit = false;
            _tree.addChildrenList(this, super.getChildren());
        }
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        return getChildren().isEmpty();
    }

}
