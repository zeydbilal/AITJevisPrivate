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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
//import javafx.scene.control.Dialogs;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisConstants;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisSample;
import org.jevis.application.dialog.ExceptionDialog;
import org.jevis.application.unit.UnitChooser;
import org.jevis.commons.unit.UnitManager;
import org.jevis.jeconfig.JEConfig;
import static org.jevis.jeconfig.JEConfig.PROGRAMM_INFO;
import org.jevis.jeconfig.plugin.unit.SimpleUnitChooser;
import org.jevis.jeconfig.sample.SampleTable;
import org.joda.time.DateTime;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class NumberWithUnit implements AttributeEditor {

    HBox box = new HBox();
    public JEVisAttribute _attribute;
    private TextField _field;
    private Node cell;
    private JEVisSample _newSample;
    private JEVisSample _lastSample;
    private boolean _hasChanged = false;

    public NumberWithUnit(JEVisAttribute att) {
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

        return box;
//        return _field;
    }

    private void buildTextFild() throws JEVisException {
        if (_field == null) {
            _field = new TextField() {
                @Override
                public void replaceText(int start, int end, String text) {
                    System.out.println("replaceText: s: " + start + " e:" + end + " text:" + text);
                    if (text.matches("[0-9]") && isVaildNumber(start, end, text)) {
                        System.out.println("is valid: " + text);
                        super.replaceText(start, end, text);
                    } else if (text.equals(",") && isVaildNumber(start, end, text)) {
                        System.out.println("is valid: " + text);
                        super.replaceText(start, end, ".");
                    } else if (text.equals(".") && isVaildNumber(start, end, text)) {
                        System.out.println("is valid: " + text);
                        super.replaceText(start, end, ".");
                    }
//                    super.replaceText(start, end, "");
                }

                private boolean isVaildNumber(int start, int end, String text) {
                    try {

                        String beginning = "";
                        String endstring = "";
                        if (start > 0) {
                            beginning = _field.getText().substring(0, start);
                        }
                        if (end < _field.getText().length()) {
                            endstring = _field.getText().substring(end, _field.getText().length());
                        }
                        String substring = _field.getText().substring(start, end);

                        double number = Double.valueOf(beginning + text + endstring);
                        return true;
                    } catch (NumberFormatException nex) {
                        System.out.println("is not an number: " + nex);
                        return false;
                    }
                }

                private boolean isVaildNumber() {
                    try {

                        double number = Double.valueOf(_field.getText());
                        return true;
                    } catch (NumberFormatException nex) {
                        System.out.println("nits not an number: " + nex);
                        return false;
                    }
                }

                @Override
                public void replaceSelection(String text) {
                    System.out.println("replace text");
                    if (text.matches("[0-9]*") && isVaildNumber()) {
                        super.replaceSelection(text);
                    } else if (text.equals(",") && isVaildNumber()) {
                        super.replaceSelection(".");
                    } else if (text.equals(".") && isVaildNumber()) {
                        super.replaceSelection(".");
                    }
//                    super.replaceSelection("");
                }
            };
//            _field.setRestrict("[0-9]");
//            _field.setRestrict("^[0-9](.[0-9])*$");
            _field.setPrefWidth(500);//TODO: remove this workaround 

            System.out.println(_attribute.getName() + " has samples: " + _attribute.hasSample());
            if (_attribute.hasSample()) {
                System.out.println("ls: " + _attribute.getLatestSample());
                System.out.println("Last sample: " + _attribute.getLatestSample().getValueAsDouble());
                _field.setText(_attribute.getLatestSample().getValueAsDouble() + "");

                _lastSample = _attribute.getLatestSample();
            }

            _field.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                    try {
                        if (newPropertyValue) {
                        } else {
                            if (_lastSample != null) {
                                if (!_lastSample.getValueAsString().equals(_field.getText())) {
                                    _hasChanged = true;
                                } else {
                                    _hasChanged = false;
                                }
                            } else {
                                if (!_field.getText().equals("")) {
                                    _hasChanged = true;
                                }
                            }

                            if (_hasChanged) {
                                try {
                                    _newSample = _attribute.buildSample(new DateTime(), _field.getText());
                                } catch (JEVisException ex) {
                                    Logger.getLogger(NumberWithUnit.class.getName()).log(Level.SEVERE, null, ex);

                                    ExceptionDialog dia = new ExceptionDialog();
                                    dia.show(JEConfig.getStage(), "Error", "Could commit changes to Server", ex, PROGRAMM_INFO);
                                }
                            }
                        }
                    } catch (Exception ex) {

                    }

                }
            });
            _field.setPrefWidth(500);
            _field.setId("attributelabel");
            _field.setAlignment(Pos.CENTER_RIGHT);

            Tooltip tooltip = new Tooltip();
            try {
                tooltip.setText(_attribute.getType().getDescription());
                tooltip.setGraphic(JEConfig.getImage("1393862576_info_blue.png", 30, 30));
                _field.setTooltip(tooltip);
            } catch (JEVisException ex) {
                Logger.getLogger(NumberWithUnit.class.getName()).log(Level.SEVERE, null, ex);
            }

            UnitChooser uc = new UnitChooser(_attribute.getType().getUnit(), 1);
            TextField tf = new TextField("kWh");
            tf.setDisable(true);
            tf.setPrefWidth(50);

            System.out.println("formtetd unit: " + UnitManager.getInstance().formate(_attribute.getUnit()));
            final Button unitb = new Button(UnitManager.getInstance().formate(_attribute.getType().getUnit()));
            unitb.setPrefWidth(60);
            unitb.setPrefHeight(22);
            unitb.setStyle("-fx-background-radius: 0 10 10 0; -fx-base: rgba(75, 106, 139, 0.89);");
            unitb.setAlignment(Pos.BOTTOM_LEFT);
            _field.setStyle("-fx-background-radius: 3 0 0 3;");

            unitb.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {

                    final Point2D nodeCoord = unitb.localToScene(0.0, 0.0);
                    SimpleUnitChooser suc = new SimpleUnitChooser();
                    try {
                        if (suc.show(nodeCoord, "Select Unit", _attribute) == SimpleUnitChooser.Response.YES) {
                            unitb.setText(suc.getPrefix() + UnitManager.getInstance().formate(suc.getUnit()));
                            _attribute.setUnit(suc.getUnit());
                        }
                    } catch (JEVisException ex) {
                        Logger.getLogger(NumberWithUnit.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("ubutton: " + unitb.getHeight());
                }
            });

            HBox.setHgrow(_field, Priority.ALWAYS);
            Button chartView = new Button();
            try {
                if (_attribute.getType().getValidity() == JEVisConstants.Validity.AT_DATE) {
                    chartView = new Button();
                    chartView.setGraphic(JEConfig.getImage("1394566386_Graph.png", 20, 20));
                    chartView.setStyle("-fx-padding: 0 2 0 2;-fx-background-insets: 0;-fx-background-radius: 0;-fx-background-color: transparent;");

                    chartView.setMaxHeight(_field.getHeight());
                    chartView.setMaxWidth(20);

                    box.getChildren().add(chartView);
                    HBox.setHgrow(chartView, Priority.NEVER);

                    chartView.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent t) {
                            Stage dialogStage = new Stage();
                            dialogStage.setTitle("Sample Editor");
                            HBox root = new HBox();

                            root.getChildren().add(new SampleTable(_attribute));

                            Scene scene = new Scene(root);
                            scene.getStylesheets().add("/styles/Styles.css");
                            dialogStage.setScene(scene);
                            dialogStage.show();

                        }
                    });

                }
            } catch (Exception ex) {
                Logger.getLogger(NumberWithUnit.class.getName()).log(Level.SEVERE, null, ex);
            }
            box.getChildren().setAll(chartView, _field, unitb);

        }
    }
}
