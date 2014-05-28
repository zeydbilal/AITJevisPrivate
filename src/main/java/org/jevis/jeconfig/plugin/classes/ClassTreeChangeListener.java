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

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
//import javafx.scene.control.Dialogs;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jevis.application.dialog.ExceptionDialog;
import org.jevis.jeconfig.JEConfig;
import static org.jevis.jeconfig.JEConfig.PROGRAMM_INFO;

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
    public void changed(ObservableValue<? extends TreeItem<String>> ov, TreeItem<String> t, final TreeItem<String> t1) {
        try {

            BorderPane ap = new BorderPane();

//            ap.setStyle("-fx-background-color: blue;");
            StackPane sp = new StackPane();
//            sp.setAlignment(Pos.CENTER);
            final ProgressIndicator pi = new ProgressIndicator();
            pi.setMaxWidth(50d);
            pi.setMinHeight(50d);

            sp.getChildren().add(pi);
            ap.setCenter(sp);
            _editorPane.getChildren().clear();
            _editorPane.getChildren().add(ap);
            VBox.setVgrow(ap, Priority.ALWAYS);

            final Thread animation = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException ex) {

                    }
                    Platform.runLater(new Runnable() {
                        public void run() {
                            pi.setProgress(-1);
                        }
                    });
                }
            };
            animation.start();

            new Thread() {

                // runnable for that thread
                @Override
                public void run() {
                    try {
                        //test simulate long loading time
//                        try {Thread.sleep(1000);} catch (InterruptedException ex) {}

                        ClassItem item = (ClassItem) t1;
                        _item = item;

                        final Node editor = _editor.buildEditor(item.getObject());
                        animation.interrupt();

                        // update ProgressIndicator on FX thread
                        Platform.runLater(new Runnable() {

                            @Override
                            public void run() {
                                _editorPane.getChildren().clear();
                                _editorPane.getChildren().add(editor);
                                VBox.setVgrow(editor, Priority.ALWAYS);
                            }
                        });

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }.start();

        } catch (Exception ex) {
            ExceptionDialog dia = new ExceptionDialog();
            dia.show(JEConfig.getStage(), "Error", "Error in class tree", ex, PROGRAMM_INFO);
        }
    }

    public ClassItem getCurrentItem() {
        return _item;
    }

    public ClassEditor getCurrentEditor() {
        return _editor;
    }
}
