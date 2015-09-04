/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import java.util.Optional;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.jevis.api.JEVisObject;
import org.jevis.jeconfig.plugin.object.ObjectTree;

/**
 *
 * @author CalisZ
 */
//192.71.247.119
public class WizardMain extends Wizard {

//    public static enum Type {
//
//        NEW, RENAME
//    };
//
//    public static enum Response {
//
//        NO, YES, CANCEL
//    };
    private JEVisObject parentObject;
    private ObjectTree tree;
//    private Response response = Response.CANCEL;
    private WizardStartPane wizardStartPane;
    private ManualWizardStep1 manualStep1;
    private ManualWizardStep2 manualStep2;
    private ManualWizardStep3 manualStep3;
    private ManualWizardStep4 manualStep4;

    private AutomatedWizardStep1 automatedWizardStep1 = new AutomatedWizardStep1();

    public WizardMain(JEVisObject parentObject,ObjectTree tree) {
        setParentObject(parentObject);
        this.tree=tree;
        wizardStartPane = new WizardStartPane(parentObject);
        manualStep1 = new ManualWizardStep1(parentObject,tree);
        manualStep2 = new ManualWizardStep2(parentObject);
        manualStep3 = new ManualWizardStep3(parentObject);
        manualStep4 = new ManualWizardStep4(parentObject);

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
                //FIXME for Template Based
                return currentPage != manualStep4 && currentPage != automatedWizardStep1;
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
                } else if (currentPage.equals(manualStep3)) {
                    return manualStep4;
                } else if (currentPage.equals(wizardStartPane) && wizardStartPane.getControl().equals("Automated")) {
                    return automatedWizardStep1;
                } else {
                    return null;
                }
            }
        };
        setFlow(flow);
    }

//    public Response show(Stage owner, final JEVisClass jclass, final JEVisObject parent, boolean fixClass, Type type, String objName) {
//        ObservableList<JEVisClass> options = FXCollections.observableArrayList();
//
//        try {
//            if (type == Type.NEW) {
//                options = FXCollections.observableArrayList(parent.getAllowedChildrenClasses());
//            }
//        } catch (JEVisException ex) {
//            Logger.getLogger(CreateTable.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return response;
//    }
    public JEVisObject getParentObject() {
        return this.parentObject;
    }

    public void setParentObject(JEVisObject parentObject) {
        this.parentObject = parentObject;
    }
}
