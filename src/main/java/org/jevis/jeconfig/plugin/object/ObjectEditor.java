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
import javafx.scene.Node;
import javafx.scene.control.Accordion;
//import javafx.scene.control.Dialogs;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import org.jevis.api.JEVisObject;
import org.jevis.application.dialog.ConfirmDialog;
import org.jevis.jeconfig.Constants;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.plugin.object.extension.MemberExtension;
import org.jevis.jeconfig.plugin.object.extension.RootExtension;
import org.jevis.jeconfig.plugin.object.extension.ShareExtension;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ObjectEditor {

    private JEVisObject _currentObject = null;
    private List<ObjectEditorExtension> extensions = new LinkedList<>();
    private boolean _saved = true;
    private String _lastOpenEditor = "";

//    private AnchorPane _view;
//    private LoadPane _view;
    private AnchorPane _view;

    public ObjectEditor() {
        _view = new AnchorPane();
//        _view = new LoadPane(false);
        _view.setId("objecteditorpane");
        _view.getStylesheets().add("/styles/Styles.css");
        _view.setStyle("-fx-background-color: " + Constants.Color.LIGHT_GREY2);
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
        if (!_saved && _currentObject != null && !Objects.equals(obj.getID(), _currentObject.getID())) {

            List<ObjectEditorExtension> needSave = new ArrayList<>();

            _saved = true;
            for (ObjectEditorExtension extension : extensions) {
                if (extension.needSave()) {
                    needSave.add(extension);
                }
            }

            if (!needSave.isEmpty()) {
                //workaround for fast saving without requesting
//                commitAll();

                ConfirmDialog dia = new ConfirmDialog();
                ConfirmDialog.Response re = dia.show(JEConfig.getStage(), "Save", "Save Attribute Changes", "Changes will be lost if not saved, do you want to save now?");
                if (re == ConfirmDialog.Response.YES) {
                    commitAll();
                } else {
                    _saved = true;
                }
            }

        }
    }

    public Node getView() {
        return _view;
    }

    public void setObject(final JEVisObject obj) {
        checkIfSaved(obj);
        _currentObject = obj;
        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                Accordion accordion = new Accordion();

                List<TitledPane> taps = new ArrayList<>();
                extensions = new ArrayList<>();
                extensions.add(new GenericAttributeExtension(obj));
                extensions.add(new MemberExtension(obj));
                extensions.add(new ShareExtension(obj));
                extensions.add(new RootExtension(obj));

                for (final ObjectEditorExtension ex : extensions) {
                    if (ex.isForObject(obj)) {
//                        System.out.println("Extension " + ex.getTitel() + " is for Object: " + obj.getName());
                        TitledPane newTab = new TitledPane(ex.getTitel(), ex.getView());
                        newTab.setAnimated(false);
                        taps.add(newTab);

                        newTab.expandedProperty().addListener(new ChangeListener<Boolean>() {

                            @Override
                            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                                if (t1) {
                                    try {
                                        JEConfig.loadNotification(true);
//                                        System.out.println("Expansion is visible: " + ex.getTitel());
                                        ex.setVisible();
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

                //load the last Extensions for the new object
                boolean foundTab = false;
                if (!taps.isEmpty()) {

                    for (ObjectEditorExtension ex : extensions) {
                        if (ex.getTitel().equals(_lastOpenEditor)) {
                            ex.setVisible();
                        }
                    }
                    for (TitledPane tap : taps) {
                        if (tap.getText().equals(_lastOpenEditor)) {
                            accordion.setExpandedPane(tap);
                            foundTab = true;
                        }
                    }

                }
                if (!foundTab) {
                    extensions.get(0).setVisible();
                    accordion.setExpandedPane(taps.get(0));
                    _lastOpenEditor = extensions.get(0).getTitel();
                }

                _view.getChildren().setAll(accordion);
            }
        });

    }

}
