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

import org.jevis.jeconfig.plugin.object.extension.GenericAttributeExtension;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
//import javafx.scene.control.Dialogs;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import org.jevis.api.JEVisObject;
import org.jevis.application.dialog.ConfirmDialog;
import org.jevis.jeconfig.Constants;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.plugin.object.extension.LinkExtension;
import org.jevis.jeconfig.plugin.object.extension.MemberExtension;
import org.jevis.jeconfig.plugin.object.extension.RootExtension;
import org.jevis.jeconfig.plugin.object.extension.PermissionExtension;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ObjectEditor {

    private JEVisObject _currentObject = null;
    private List<ObjectEditorExtension> extensions = new LinkedList<>();
    private boolean _hasChanged = true;
    private String _lastOpenEditor = "";

//    private AnchorPane _view;
//    private LoadPane _view;
    private AnchorPane _view;
//    LoadingPane loaderP = new LoadingPane();

    public ObjectEditor() {
        _view = new AnchorPane();
//        _view = new LoadPane(false);
        _view.setId("objecteditorpane");
        _view.getStylesheets().add("/styles/Styles.css");
        _view.setStyle("-fx-background-color: " + Constants.Color.LIGHT_GREY2);

//        AnchorPane.setTopAnchor(loaderP, 0.0);
//        AnchorPane.setRightAnchor(loaderP, 0.0);
//        AnchorPane.setLeftAnchor(loaderP, 0.0);
//        AnchorPane.setBottomAnchor(loaderP, 0.0);
//        _view.getChildren().setAll(loaderP);
//        _view.setStyle("-fx-background-color: " + Constants.Color.LIGHT_GREY2);
//        _view.setContent(_root);
    }

    public void commitAll() {
        for (ObjectEditorExtension extension : extensions) {
            if (extension.needSave()) {
                extension.save();
            }
        }
    }

    public void checkIfSaved(JEVisObject obj) {
//        System.out.println("checkIfSaved: " + obj);
        if (_currentObject != null && !Objects.equals(obj.getID(), _currentObject.getID())) {

            List<ObjectEditorExtension> needSave = new ArrayList<>();

//            _hasChanged = true;
            for (ObjectEditorExtension extension : extensions) {
                if (extension.needSave()) {
                    System.out.println("extension need save: " + extension.getTitel());
                    needSave.add(extension);
                }
            }
//            System.out.println("needSave.size: " + needSave.size());

            if (!needSave.isEmpty()) {
                //workaround for fast saving without requesting
//                commitAll();
//                System.out.println("need save");
                ConfirmDialog dia = new ConfirmDialog();
                ConfirmDialog.Response re = dia.show(JEConfig.getStage(), "Save", "Save Attribute Changes", "Changes will be lost if not saved, do you want to save now?");
                if (re == ConfirmDialog.Response.YES) {
                    commitAll();
                } else {
                    _hasChanged = false;
                }
            }

        }
    }

    public Node getView() {
        return _view;
    }

    public void setObject(final JEVisObject obj) {
        Task<Void> load = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                loadObject(obj);
                return null;
            }
        };

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                JEConfig.getStage().getScene().setCursor(Cursor.WAIT);

            }
        });

        load.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent event) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        JEConfig.getStage().getScene().setCursor(Cursor.DEFAULT);

                    }
                });
            }
        });

        new Thread(load).start();

    }

    public void loadObject(final JEVisObject obj) {
        checkIfSaved(obj);
        _currentObject = obj;
        Platform.runLater(new Runnable() {

            @Override
            public void run() {

//                AnchorPane content = new AnchorPane();
                Accordion accordion = new Accordion();

                List<TitledPane> taps = new ArrayList<>();
                extensions = new ArrayList<>();
                extensions.add(new GenericAttributeExtension(obj));
                extensions.add(new MemberExtension(obj));
                extensions.add(new PermissionExtension(obj));
                extensions.add(new RootExtension(obj));
                extensions.add(new LinkExtension(obj));

                for (final ObjectEditorExtension ex : extensions) {
                    if (ex.isForObject(obj)) {
//                        System.out.println("Extension " + ex.getTitel() + " is for Object: " + obj.getName());
                        TitledPane newTab = new TitledPane(ex.getTitel(), ex.getView());
                        newTab.setAnimated(false);
                        taps.add(newTab);
                        ex.getValueChangedProperty().addListener(new ChangeListener<Boolean>() {

                            @Override
                            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                                if (t1) {
                                    System.out.println("Valuechanged for: " + ex.getTitel());
                                    _hasChanged = t1;//TODO: enable/disbale the save button
                                }
                            }
                        });

                        newTab.expandedProperty().addListener(new ChangeListener<Boolean>() {

                            @Override
                            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                                if (t1) {
                                    try {
                                        JEConfig.loadNotification(true);
                                        System.out.println("Expansion is visible: " + ex.getTitel());
//                                        loaderP.setProgress(1);
                                        ex.setVisible();
//                                        loaderP.setContent(content);

//                                        updateView(content, ex);
//                                        loaderP.setProgress(100);
                                        _lastOpenEditor = ex.getTitel();
                                        JEConfig.loadNotification(false);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        });

                    }
                }

                accordion.getPanes().addAll(taps);
                AnchorPane.setTopAnchor(accordion, 0.0);
                AnchorPane.setRightAnchor(accordion, 0.0);
                AnchorPane.setLeftAnchor(accordion, 0.0);
                AnchorPane.setBottomAnchor(accordion, 0.0);
//                content.getChildren().add(accordion);

                //load the last Extensions for the new object
                boolean foundTab = false;
                if (!taps.isEmpty()) {

                    for (ObjectEditorExtension ex : extensions) {
                        if (ex.getTitel().equals(_lastOpenEditor)) {
                            ex.setVisible();
//                            updateView(content, ex);
                        }
                    }
                    for (TitledPane tap : taps) {
                        if (tap.getText().equals(_lastOpenEditor)) {
                            accordion.setExpandedPane(tap);
                            foundTab = true;
                        }
                    }

                }
//                FXProgressPanel loaderP = new FXProgressPanel(true, accordion);
//                _view.getChildren().setAll(loaderP);

                if (!foundTab) {
//                    updateView(content, extensions.get(0));
                    extensions.get(0).setVisible();
                    accordion.setExpandedPane(taps.get(0));
                    _lastOpenEditor = extensions.get(0).getTitel();
                }
//                loaderP.setContent(content);
                _view.getChildren().setAll(accordion);
            }
        });

    }

}
