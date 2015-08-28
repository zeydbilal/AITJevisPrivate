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
//192.71.247.119
public class WizardMain extends Wizard {

//    private ObservableList<WizardPane> listPages = FXCollections.observableArrayList();
    public WizardMain() {
        setTitle("JEVIS Wizard");
        initWizard();
    }

    public void initWizard() {

        WizardStartPane wizardStartPane = new WizardStartPane();
//        listPages.add(wizardStartPane);
//
//        LinearFlow fl = new LinearFlow(listPages);
//        setFlow(fl);

        LinearFlow fl = new LinearFlow(wizardStartPane);
        setFlow(fl);
        
        // Wie funktioniert wizardAdvance
//        ManualWizardStep1 manualStep1 = new ManualWizardStep1();
//        fl.advance(wizardStartPane);
        
        
//        //ManualSteps
//        if (wizardStartPane.getControl().equals("Manual")) {
//            ManualWizardStep1 manualStep1 = new ManualWizardStep1();
//            ManualWizardStep2 manualStep2 = new ManualWizardStep2();
//            ManualWizardStep3 manualStep3 = new ManualWizardStep3();
//            listPages.add(manualStep1);
//            fl = new LinearFlow(listPages);
//            setFlow(fl);
//            //AutomatedSteps
//        } else if (wizardStartPane.getControl().equals("Automated")) {
//            //FIXME
//            AutomatedWizardStep1 automatedStep1 = new AutomatedWizardStep1();
//            //TemplatebasedSteps   
//        } else if (wizardStartPane.getControl().equals("Template Based")) {
//            //TODO
//        }

    }
}
