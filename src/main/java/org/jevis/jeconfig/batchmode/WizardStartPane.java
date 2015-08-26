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
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.dialog.WizardPane;
import org.jevis.jeconfig.JEConfig;

/**
 *
 * @author CalisZ
 */
public class WizardStartPane extends WizardPane {

    private VBox vbox;
    private GridPane gridPane;

    public WizardStartPane() {
        setMinSize(500, 500);
        //FIXME remove
//        Stage stage = (Stage) this.getScene().getWindow();
//        stage.getIcons().add(new Image(JEConfig.class.getResourceAsStream(("/icons/create_wizard.png"))));
    }

    public VBox getInit() {
        vbox = new VBox();

        Label image = new Label();
        image.setGraphic(JEConfig.getImage("create_wizard.png", 100, 100));

        Label index = new Label();
        index.setText("JEVIS setup ");

        ToggleGroup group = new ToggleGroup();
        RadioButton button1 = new RadioButton("Manual");
        button1.setToggleGroup(group);
        button1.setSelected(true);

        RadioButton button2 = new RadioButton("Automated");
        button2.setToggleGroup(group);

        RadioButton button3 = new RadioButton("Template Based");
        button3.setToggleGroup(group);

        vbox.setSpacing(30);

        vbox.getChildren().addAll(image, index, button1, button2, button3);
        vbox.setPadding(new Insets(200, 10, 10, 20));

        return vbox;
    }
}
