/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.controlsfx.dialog.WizardPane;
import org.jevis.jeconfig.JEConfig;

/**
 *
 * @author CalisZ
 */
public class WizardStartPane extends WizardPane {

    private VBox vbox;
    private GridPane gridPane;
    private RadioButton button1 = new RadioButton("Manual");
    private RadioButton button2 = new RadioButton("Automated");
    private RadioButton button3 = new RadioButton("Template Based");
    private String control;

    public WizardStartPane() {
        setMinSize(500, 500);
        //INFO
        //Stage stage = (Stage) this.getScene().getWindow();
        //stage.setTitle("JEVIS Wizard");

        setContent(getInit());
        setGraphic(JEConfig.getImage("create_wizard.png", 100, 100));

    }

    public VBox getInit() {
        vbox = new VBox();

        Label index = new Label();
        index.setText("JEVIS setup ");

        ToggleGroup group = new ToggleGroup();

        button1.setToggleGroup(group);
        button1.setSelected(true);

        button2.setToggleGroup(group);
        button3.setToggleGroup(group);

        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov,
                    Toggle old_toggle, Toggle new_toggle) {
                if (button1.isSelected()) {
                    setControl(button1.getText());
                } else if (button2.isSelected()) {
                    setControl(button2.getText());
                } else if (button3.isSelected()) {
                    setControl(button3.getText());
                }
            }
        });

        vbox.setSpacing(30);

        vbox.getChildren().addAll(index, button1, button2, button3);
        vbox.setPadding(new Insets(200, 10, 10, 20));

        return vbox;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public String getControl() {
        return control;
    }
}
