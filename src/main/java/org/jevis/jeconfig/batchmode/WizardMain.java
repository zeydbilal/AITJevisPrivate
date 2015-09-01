/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import java.util.Optional;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

/**
 *
 * @author CalisZ
 */
//192.71.247.119
public class WizardMain extends Wizard {

    private WizardStartPane wizardStartPane = new WizardStartPane();

    private ManualWizardStep1 manualStep1 = new ManualWizardStep1();
    private ManualWizardStep2 manualStep2 = new ManualWizardStep2();
    private ManualWizardStep3 manualStep3 = new ManualWizardStep3();

    private AutomatedWizardStep1 automatedWizardStep1 = new AutomatedWizardStep1();

    public WizardMain() {
        setTitle("JEVIS Wizard");
        initWizard();
    }

    private void initWizard() {

        Wizard.Flow flow = new Wizard.Flow() {

            @Override
            public Optional<WizardPane> advance(WizardPane currentPage) {
                return Optional.of(getNext(currentPage));
            }

            @Override
            public boolean canAdvance(WizardPane currentPage) {
                //FIXME
                return currentPage != manualStep3 && currentPage != automatedWizardStep1;

            }

            private WizardPane getNext(WizardPane currentPage) {
                if (currentPage == null) {
                    return wizardStartPane;
                } else if (currentPage.equals(wizardStartPane) && wizardStartPane.getControl().equals("Manual")) {
                    return manualStep1;
                } else if (currentPage.equals(manualStep1)) {
                    return manualStep2;
                } else if (currentPage.equals(manualStep2)) {
                    return manualStep3;
                } else if (currentPage.equals(wizardStartPane) && wizardStartPane.getControl().equals("Automated")) {
                    return automatedWizardStep1;
                } else {
                    return null;
                }
            }
        };

        setFlow(flow);

    }
}
