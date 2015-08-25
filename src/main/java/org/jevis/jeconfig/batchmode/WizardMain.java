/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import org.controlsfx.dialog.Wizard;

/**
 *
 * @author Bilal
 */
public class WizardMain {

    public WizardMain() {
        Wizard wizard = new Wizard();

        WizardStartPane wizardStartPane = new WizardStartPane();

        WizardStep1 step1 = new WizardStep1();

        wizardStartPane.setContent(wizardStartPane.getInit());

        wizard.setFlow(new Wizard.LinearFlow(wizardStartPane, step1));

        wizard.showAndWait();
    }

}
