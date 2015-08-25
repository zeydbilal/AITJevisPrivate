/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.controlsfx.dialog.WizardPane;

/**
 *
 * @author CalisZ
 */
public class WizardStartPane extends WizardPane {

    private VBox vbox;
    private GridPane gridPane;

    public WizardStartPane() {
        setMinSize(500, 500);

    }

    public VBox getInit() {
        vbox = new VBox();

        Label index = new Label();
        index.setText("JEVIS setup ");

        ToggleGroup group = new ToggleGroup();
        RadioButton button1 = new RadioButton("Manual");

        button1.setToggleGroup(group);
        button1.setSelected(true);
        RadioButton button2 = new RadioButton("Automated");
        button2.setToggleGroup(group);

        vbox.setSpacing(30);

        vbox.getChildren().addAll(index, button1, button2);
        vbox.setPadding(new Insets(200, 10, 10, 20));

        return vbox;
    }

}
