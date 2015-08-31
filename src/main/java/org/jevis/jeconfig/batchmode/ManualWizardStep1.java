/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisObject;

/**
 *
 * @author CalisZ
 */
public class ManualWizardStep1 extends WizardPane {

    public static enum Type {

        NEW, RENAME
    };

    public static enum Response {

        NO, YES, CANCEL
    };

    private Response response = Response.CANCEL;
    private VBox vbox;

    public ManualWizardStep1() {
        setMinSize(500, 500);
        setContent(getInit());
        setGraphic(null);
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

    //or private ? 
    public VBox getInit() {
        vbox = new VBox();

        return vbox;
    }

    public Response show(Stage owner, final JEVisClass jclass, final JEVisObject parent, boolean fixClass, Type type, String objName) {

        return response;
    }

}
