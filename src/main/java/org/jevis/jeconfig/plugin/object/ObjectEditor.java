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

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
//import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisConstants.PrimitiveType;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.application.dialog.ConfirmDialog;
import org.jevis.application.dialog.ExceptionDialog;
import org.jevis.jeconfig.Constants;
import org.jevis.jeconfig.JEConfig;
import static org.jevis.jeconfig.JEConfig.PROGRAMM_INFO;
import org.jevis.jeconfig.plugin.object.attribute.AttributeEditor;
import org.jevis.jeconfig.plugin.object.attribute.BooleanValueEditor;
import org.jevis.jeconfig.plugin.object.attribute.FileValueEditor;
import org.jevis.jeconfig.plugin.object.attribute.StringValueEditor;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ObjectEditor {

    private JEVisObject _currentObject = null;
    private List<AttributeEditor> _editors = new LinkedList<>();
    private boolean _saved = true;

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
        try {
            for (AttributeEditor editor : _editors) {
                editor.commit();
                _saved = true;
            }
        } catch (JEVisException ex) {
            Logger.getLogger(ObjectEditor.class.getName()).log(Level.SEVERE, null, ex);

            ExceptionDialog dia = new ExceptionDialog();
            dia.show(JEConfig.getStage(), "Error", "Could not commit to Server", ex, PROGRAMM_INFO);
        }
    }

    public void checkIfSaved(JEVisObject obj) {
        if (!_saved && _currentObject != null && obj.getID() != _currentObject.getID()) {
            _saved = true;
            for (AttributeEditor editor : _editors) {
                if (editor.hasChanged()) {
                    _saved = false;
                    System.out.println("has changed: ");
                }
            }
            if (!_saved) {

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
        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                AnchorPane root = new AnchorPane();
                final Accordion accordion = new Accordion();

                _saved = false;
                _editors = new LinkedList<>();
                _currentObject = obj;

                GridPane gridPane = new GridPane();
//        gridPane.setPadding(new Insets(20, 0, 20, 20));
                gridPane.setPadding(new Insets(5, 0, 20, 20));
                gridPane.setHgap(7);
                gridPane.setVgap(7);

                try {
                    int coloum = 0;
                    for (JEVisAttribute att : obj.getAttributes()) {
                        AttributeEditor editor = null;

                        switch (att.getPrimitiveType()) {
                            case PrimitiveType.STRING:
                                editor = new StringValueEditor(att);
                                break;
                            case PrimitiveType.BOOLEAN:
                                editor = new BooleanValueEditor(att);
                                break;
                            case PrimitiveType.FILE:
                                editor = new FileValueEditor(att);
                                break;
                            default:
                                editor = new StringValueEditor(att);
                                break;

                        }

                        _editors.add(editor);

                        Label name = new Label("*Missing_Name*");

                        name.setId("attributelabel");

                        GridPane.setHalignment(name, HPos.LEFT);
                        gridPane.add(name, 0, coloum);
                        gridPane.add(editor.getEditor(), 1, coloum);

                        name.setText(att.getName() + ":");

//                if (att.hasSample()) {
//                    value.setText(att.getLatestSample().getValueAsString());
//                }
                        coloum++;
                    }
                } catch (JEVisException ex) {
                    Logger.getLogger(ObjectEditor.class.getName()).log(Level.SEVERE, null, ex);
                }

                root.getChildren().add(gridPane);
                AnchorPane.setTopAnchor(gridPane, 0.0);
                AnchorPane.setRightAnchor(gridPane, 0.0);
                AnchorPane.setLeftAnchor(gridPane, 0.0);
                AnchorPane.setBottomAnchor(gridPane, 0.0);

                ScrollPane sp = new ScrollPane();
                sp.setContent(root);
                final TitledPane t1 = new TitledPane("Attributes", sp);
                accordion.getPanes().addAll(t1);

                sp.setStyle("-fx-background-color: " + Constants.Color.LIGHT_GREY2);

                t1.setAnimated(false);

                AnchorPane.setTopAnchor(accordion, 0.0);
                AnchorPane.setRightAnchor(accordion, 0.0);
                AnchorPane.setLeftAnchor(accordion, 0.0);
                AnchorPane.setBottomAnchor(accordion, 0.0);
                accordion.setExpandedPane(t1);

//                System.out.println("animation interrupt");
//        Platform.runLater(new Runnable() {
//
//            @Override
//            public void run() {
//                animation.interrupt();
                _view.getChildren().setAll(accordion);
                //            }
            }
        });

    }

}
