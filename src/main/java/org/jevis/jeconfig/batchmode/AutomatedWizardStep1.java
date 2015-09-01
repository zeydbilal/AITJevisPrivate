/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

/**
 *
 * @author CalisZ
 */
public class AutomatedWizardStep1 extends WizardPane {

    private VBox vbox;

    public AutomatedWizardStep1() {
        setMinSize(500, 500);
        setContent(getInit());
        setGraphic(null);
    }

    private VBox getInit() {
        vbox = new VBox();
        Label index = new Label();
        index.setText("AutomatedWizardStep1");

        vbox.setSpacing(30);

        vbox.getChildren().addAll(index);
        vbox.setPadding(new Insets(200, 10, 10, 20));

        return vbox;
    }

    @Override
    public void onEnteringPage(Wizard wizard) {

        ObservableList<ButtonType> list = getButtonTypes();

        for (ButtonType type : list) {
            if (type.getButtonData().equals(ButtonBar.ButtonData.BACK_PREVIOUS)) {
                Node prev = lookupButton(type);
                prev.visibleProperty().setValue(Boolean.FALSE);

            }
        }
    }
}
