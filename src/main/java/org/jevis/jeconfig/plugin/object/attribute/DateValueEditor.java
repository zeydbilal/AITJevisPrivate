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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
//import javafx.scene.control.Dialogs;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javax.swing.text.DateFormatter;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisConstants;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisSample;
import org.jevis.jeconfig.JEConfig;
import org.joda.time.DateTime;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class DateValueEditor implements AttributeEditor {

    HBox box = new HBox();
    public JEVisAttribute _attribute;
    private DatePicker _datePicker;
    private JEVisSample _newSample;
    private JEVisSample _lastSample;
    private final BooleanProperty _changed = new SimpleBooleanProperty(false);
    private boolean _readOnly = true;

    public DateValueEditor(JEVisAttribute att) {
        _attribute = att;
    }

    @Override
    public boolean hasChanged() {
//        System.out.println(_attribute.getName() + " changed: " + _hasChanged);
        return _changed.getValue();
    }

    @Override
    public BooleanProperty getValueChangedProperty() {
        return _changed;
    }

    @Override
    public void setReadOnly(boolean canRead) {
        _readOnly = canRead;
    }

//    @Override
//    public void setAttribute(JEVisAttribute att) {
//        _attribute = att;
//    }
    @Override
    public void commit() throws JEVisException {
        System.out.println("request Commit");
        if (hasChanged() && _newSample != null) {

            //TODO: check if tpye is ok, maybe better at imput time
            System.out.println("Commit date: " + _newSample.getValueAsString());
            _newSample.commit();
        }
    }

    @Override
    public Node getEditor() {
        try {
            buildEditor();
        } catch (Exception ex) {

        }

        return box;
//        return _field;
    }

    private void buildEditor() throws JEVisException {
        if (_datePicker == null) {
            _datePicker = new DatePicker();
            _datePicker.setPrefWidth(150);//TODO: remove this workaround
            _datePicker.setShowWeekNumbers(true);
            _datePicker.setId("attributelabel");

            if (_attribute.getLatestSample() != null) {
                try {
                    _datePicker.setValue(LocalDate.parse(_attribute.getLatestSample().getValueAsString(), DateTimeFormatter.ISO_DATE));
                } catch (Exception ex) {
                }
//                _field.setText(_attribute.getLatestSample().getValueAsString());
                _lastSample = _attribute.getLatestSample();
            } else {

//                _field.setText("");
            }

            _datePicker.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    System.out.println("setOnAction");
                    try {
                        if (_lastSample == null) {
                            System.out.println("new Value");
                            _newSample = _attribute.buildSample(new DateTime(), _datePicker.getValue().format(DateTimeFormatter.ISO_DATE));
                            _changed.setValue(true);
                        } else {
                            System.out.println("change existing value");
                            _newSample.setValue(_datePicker.getValue().format(DateTimeFormatter.ISO_DATE));
                            _changed.setValue(true);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(NumberWithUnit.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

//            _datePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
//
//                @Override
//                public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
//                    System.out.println("valueProperty");
//                    try {
//                        if (_lastSample == null) {
//                            System.out.println("new Value");
//                            _newSample = _attribute.buildSample(new DateTime(), newValue.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
//                            _changed.setValue(true);
//                            System.out.println("value changed");
//                        } else {
//                            System.out.println("change existing value");
//                            _lastSample.setValue(newValue.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
//                            _changed.setValue(true);
//                        }
//                    } catch (Exception ex) {
//                        Logger.getLogger(NumberWithUnit.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            });
            if (_attribute.getType().getDescription() != null && !_attribute.getType().getDescription().isEmpty()) {
                Tooltip tooltip = new Tooltip();
                try {
                    tooltip.setText(_attribute.getType().getDescription());
                    tooltip.setGraphic(JEConfig.getImage("1393862576_info_blue.png", 30, 30));
                    _datePicker.setTooltip(tooltip);
                } catch (JEVisException ex) {
                    Logger.getLogger(DateValueEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            box.getChildren().add(_datePicker);
            HBox.setHgrow(_datePicker, Priority.ALWAYS);

            try {
                if (_attribute.getType().getValidity() == JEVisConstants.Validity.AT_DATE) {
                    Button chartView = new Button();
                    chartView.setGraphic(JEConfig.getImage("1394566386_Graph.png", 20, 20));
                    chartView.setStyle("-fx-padding: 0 2 0 2;-fx-background-insets: 0;-fx-background-radius: 0;-fx-background-color: transparent;");

                    chartView.setMaxHeight(_datePicker.getHeight());
                    chartView.setMaxWidth(20);

                    box.getChildren().add(chartView);
                    HBox.setHgrow(chartView, Priority.NEVER);
                }
            } catch (Exception ex) {
                Logger.getLogger(DateValueEditor.class.getName()).log(Level.SEVERE, null, ex);
            }

            _datePicker.setDisable(_readOnly);

        }
    }
}
