/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import org.controlsfx.dialog.Wizard;

/**
 *
 * @author CalisZ
 */
public class WizardMain extends Wizard {

    public WizardMain() {
        initWizard();
    }

    public void initWizard() {
        WizardStartPane wizardStartPane = new WizardStartPane();

        WizardStep1 step1 = new WizardStep1();
        WizardStep2 step2 = new WizardStep2();
        WizardStep3 step3 = new WizardStep3();

        wizardStartPane.setContent(wizardStartPane.getInit());

        setFlow(new Wizard.LinearFlow(wizardStartPane, step1));

    }
}
