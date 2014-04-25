/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.classes;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
//import javafx.scene.control.Dialogs;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import org.controlsfx.dialog.Dialogs;
import org.jevis.jeconfig.JEConfig;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ClassTreeChangeListener implements EventHandler<ActionEvent>, ChangeListener<TreeItem<String>> {

    VBox _editorPane;
    ClassItem _item;
    Button _button;
    private ClassEditor _editor;

    public ClassTreeChangeListener(VBox editorPane) {
        _editorPane = editorPane;
        _editor = new ClassEditor();
//        _button = save;//TODo replace by some kind of listener or so.....
    }

    @Override
    public void handle(ActionEvent t) {
    }

    @Override
    public void changed(ObservableValue<? extends TreeItem<String>> ov, TreeItem<String> t, TreeItem<String> t1) {
        try {
            ClassItem item = (ClassItem) t1;
            _item = item;

            _editorPane.getChildren().clear();
            _editorPane.getChildren().add(_editor.buildEditor(item.getObject()));

        } catch (Exception ex) {
//            Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);
            Dialogs.create()
                    .owner(JEConfig.getStage())
                    .title("Error")
                    .showException(ex);
        }
    }

    public ClassItem getCurrentItem() {
        return _item;
    }

    public ClassEditor getCurrentEditor() {
        return _editor;
    }
}
