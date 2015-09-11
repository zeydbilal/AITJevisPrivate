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
//This is a help class for selected objects
public class WizardSelectedObject {

    // Selected objects from tree
    private JEVisObject currentSelectedObject;
    private JEVisObject currentSelectedBuildingObject;

    public WizardSelectedObject() {
    }

    public JEVisObject getCurrentSelectedObject() {
        return currentSelectedObject;
    }

    public void setCurrentSelectedObject(JEVisObject currentSelectedObject) {
        this.currentSelectedObject = currentSelectedObject;
    }

    public JEVisObject getCurrentSelectedBuildingObject() {
        return currentSelectedBuildingObject;
    }

    public void setCurrentSelectedBuildingObject(JEVisObject currentSelectedBuildingObject) {
        this.currentSelectedBuildingObject = currentSelectedBuildingObject;
    }
}
