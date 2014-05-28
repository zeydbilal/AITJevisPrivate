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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ObjectTreeChangeListener implements EventHandler<ActionEvent>, ChangeListener<TreeItem<TreeObject>> {

//    VBox _editorPane;
//    AnchorPane _editorPane;
    TreeItem<TreeObject> _item;
    Button _button;
    private ObjectEditor _editor;

    public ObjectTreeChangeListener(ObjectEditor _editor) {
//        _editorPane = editorPane;
        this._editor = _editor;
    }

    @Override
    public void handle(ActionEvent t) {
    }

    @Override
    public void changed(ObservableValue<? extends TreeItem<TreeObject>> ov, TreeItem<TreeObject> t, TreeItem<TreeObject> t1) {
        try {
            _item = t1;
            _editor.setObject(t1.getValue().getObject());
        } catch (Exception ex) {
//            Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);
        }
    }

    public TreeItem<TreeObject> getCurrentItem() {
        return _item;
    }
}
