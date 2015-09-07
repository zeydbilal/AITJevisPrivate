/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import org.jevis.api.JEVisObject;

/**
 *
 * @author CalisZ
 */
//New parent and new child object
public class WizardSelectedObject {

    private JEVisObject selectedObject;

    public WizardSelectedObject() {
    }

    public JEVisObject getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(JEVisObject selectedObject) {
        this.selectedObject = selectedObject;
    }
}
