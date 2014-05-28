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
package org.jevis.jeconfig.plugin.object.attribute;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisSample;
import org.jevis.application.dialog.ExceptionDialog;
import org.jevis.jeconfig.JEConfig;
import static org.jevis.jeconfig.JEConfig.PROGRAMM_INFO;
import org.joda.time.DateTime;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class BooleanValueEditor implements AttributeEditor {

    public JEVisAttribute _attribute;
    private CheckBox _field;
    private JEVisSample _newSample;
    private JEVisSample _lastSample;
    private boolean _hasChanged = false;

    public BooleanValueEditor(JEVisAttribute att) {
        _attribute = att;
    }

    @Override
    public boolean hasChanged() {
//        System.out.println(_attribute.getName() + " changed: " + _hasChanged);
        return _hasChanged;
    }

//    @Override
//    public void setAttribute(JEVisAttribute att) {
//        _attribute = att;
//    }
    @Override
    public void commit() throws JEVisException {
        if (_hasChanged && _newSample != null) {

            //TODO: check if tpye is ok, maybe better at imput time
            _newSample.commit();
        }
    }

    @Override
    public Node getEditor() {
        try {
            buildTextFild();
        } catch (Exception ex) {

        }
        return _field;

    }

    private void buildTextFild() throws JEVisException {
        if (_field == null) {
            _field = new CheckBox();
            _field.setPrefWidth(500);//TODO: hmm workaround remove

            if (_attribute.hasSample() && _attribute.getLatestSample() != null) {
                _field.setSelected(_attribute.getLatestSample().getValueAsBoolean());//TODO: get default Value
                _lastSample = _attribute.getLatestSample();
            } else {
                _field.setSelected(false);//TODO: get default Value
            }

            _field.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                    try {
                        if (newPropertyValue) {
//                        System.out.println("Textfield on focus");
                        } else {
                            System.out.println("valueChanged");
                            if (_lastSample != null) {
                                System.out.println("Old Value: " + _lastSample.getValueAsBoolean());
                                System.out.println("New Value: " + _field.isSelected());
                                if (!_lastSample.getValueAsBoolean() == _field.isSelected()) {
                                    _hasChanged = true;
                                } else {
                                    _hasChanged = false;
                                }
                            } else {
                                if (_field.isSelected()) {
                                    _hasChanged = true;
                                }
                            }

                            if (_hasChanged) {
                                try {
                                    _newSample = _attribute.buildSample(new DateTime(), _field.isSelected());
                                } catch (JEVisException ex) {
                                    Logger.getLogger(BooleanValueEditor.class.getName()).log(Level.SEVERE, null, ex);
//                                    Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", null, ex);

                                    ExceptionDialog dia = new ExceptionDialog();
                                    dia.show(JEConfig.getStage(), "Error", "Could commit changes to Server", ex, PROGRAMM_INFO);
                                }
                            }
                        }
                    } catch (Exception ex) {

                    }
                }
            });

//            _field.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
//                @Override
//                public void handle(KeyEvent event) {
//                    //changed
//                    event.consume();
//
//
//
//                }
//            });
            _field.setPrefWidth(500);
            _field.setId("attributelabel");

        }
    }
}
