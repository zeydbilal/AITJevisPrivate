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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import org.jevis.jeconfig.plugin.classesnew.editor.ClassEditor;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ClassTreeChangeListener implements EventHandler<ActionEvent>, ChangeListener<TreeItem<ClassTreeObject>> {

//    VBox _editorPane;
//    AnchorPane _editorPane;
    TreeItem<ClassTreeObject> _item;
    Button _button;
    private ClassEditor _editor;

    public ClassTreeChangeListener(ClassEditor _editor) {
//        _editorPane = editorPane;
        this._editor = _editor;
    }

    @Override
    public void handle(ActionEvent t) {
    }

    @Override
    public void changed(ObservableValue<? extends TreeItem<ClassTreeObject>> ov, TreeItem<ClassTreeObject> t, TreeItem<ClassTreeObject> t1) {
        try {
            _item = t1;
            _editor.setClass(t1.getValue().getObject());
            //TODO

        } catch (Exception ex) {
//            Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);
        }
    }

    public TreeItem<ClassTreeObject> getCurrentItem() {
        return _item;
    }
}
