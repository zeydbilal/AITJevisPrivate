/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.object;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ObjectTreeChangeListener implements EventHandler<ActionEvent>, ChangeListener<TreeItem<String>> {

    VBox _editorPane;
    ObjectItem _item;
    Button _button;
    private ObjectEditor _editor;

//    public ObjectTreeChangeListener(Pane editorPane) {
//        _editorPane = editorPane;
//        _editor = new ObjectEditor();
////        _button = save;//TODo replace by some kind of listener or so.....
//    }
    public ObjectTreeChangeListener(VBox editorPane, ObjectEditor _editor) {
        _editorPane = editorPane;
        this._editor = _editor;
    }

    @Override
    public void handle(ActionEvent t) {
    }

    @Override
    public void changed(ObservableValue<? extends TreeItem<String>> ov, TreeItem<String> t, TreeItem<String> t1) {
        try {
            ObjectItem item = (ObjectItem) t1;
            _item = item;

            _editorPane.getChildren().clear();
            _editorPane.getChildren().add(_editor.buildEditor(item.getObject()));
        } catch (Exception ex) {
//            Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);
        }
    }

    public ObjectItem getCurrentItem() {
        return _item;
    }
}
