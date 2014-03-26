/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.object;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import org.jevis.jeapi.JEVisAttribute;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jeapi.JEVisConstants.*;
import org.jevis.jeconfig.JEConfig;
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

    public ObjectEditor() {
    }

    public void commitAll() {
        try {
            for (AttributeEditor editor : _editors) {
                editor.commit();
                _saved = true;
            }
        } catch (JEVisException ex) {
            Logger.getLogger(ObjectEditor.class.getName()).log(Level.SEVERE, null, ex);
            Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", null, ex);
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
                Dialogs.DialogResponse response = Dialogs.showConfirmDialog(
                        JEConfig.getStage(), "Changes will be lost if not saved, do you want to save now?",
                        "Save Attribute Changes", "Save", Dialogs.DialogOptions.OK_CANCEL);
                if (response == Dialogs.DialogResponse.OK) {
                    commitAll();
                } else {
                    _saved = true;
                }
            }

        }
    }

    public Node buildEditor(JEVisObject obj) {
        checkIfSaved(obj);

        _saved = false;
        _editors = new LinkedList<>();
        _currentObject = obj;

        GridPane gridPane = new GridPane();
//        gridPane.setPadding(new Insets(20, 0, 20, 20));
        gridPane.setPadding(new Insets(5, 0, 20, 20));
        gridPane.setHgap(7);
        gridPane.setVgap(7);

        System.out.println("selected Object: " + obj);

        try {
            System.out.println("Attribute size: " + obj.getAttributes().size());
            int coloum = 0;
            for (JEVisAttribute att : obj.getAttributes()) {
                System.out.println("Attribute-> " + att);
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
//                TextField value = new TextField();
//                value.setPrefWidth(500);
                name.setId("attributelabel");

                GridPane.setHalignment(name, HPos.LEFT);
                gridPane.add(name, 0, coloum);
                gridPane.add(editor.getEditor(), 1, coloum);

                //Fun Part
                FadeTransition ft = new FadeTransition(Duration.millis((coloum + 0.5) * 150), name);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();

                FadeTransition ft2 = new FadeTransition(Duration.millis((coloum + 0.5) * 150), editor.getEditor());
                ft2.setFromValue(0.0);
                ft2.setToValue(1.0);
                ft2.play();



                name.setText(att.getName() + ":");

//                if (att.hasSample()) {
//                    value.setText(att.getLatestSample().getValueAsString());
//                }

                coloum++;
            }
        } catch (JEVisException ex) {
            Logger.getLogger(ObjectEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        final TitledPane t1 = new TitledPane("Attributes", gridPane);
        t1.setAnimated(false);
        t1.setExpanded(true);

        return t1;
    }
}
