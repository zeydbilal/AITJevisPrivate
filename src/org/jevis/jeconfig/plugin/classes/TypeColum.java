/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.classes;

import javafx.beans.property.SimpleStringProperty;
import org.jevis.jeapi.JEVisClass;
import org.jevis.jeapi.JEVisConstants;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisConstants.*;
import org.jevis.jeapi.JEVisType;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class TypeColum {

    private SimpleStringProperty otherClass = new SimpleStringProperty("Error");
    private SimpleStringProperty type = new SimpleStringProperty("Error");
    private SimpleStringProperty direction = new SimpleStringProperty("Error");

    /**
     *
     * @param relation
     * @param jclass
     */
    public TypeColum(JEVisType type) {
        try {
            this.otherClass = new SimpleStringProperty(type.getName());
            this.type = new SimpleStringProperty(getTypeName(type.getPrimitiveType()));
            this.direction = new SimpleStringProperty(type.getGUIDisplayType());
        } catch (JEVisException ex) {
        }
    }

    private String getDirectionName(int direction) {
        switch (direction) {
            case Direction.BACKWARD:
                return "Backward";
            case Direction.FORWARD:
                return "Forward";
            default:
                return "Unknown";
        }

    }

    private String getTypeName(int type) {
        switch (type) {
            case JEVisConstants.PrimitiveType.STRING:
                return "String";
            case JEVisConstants.PrimitiveType.DOUBLE:
                return "Double";
            case JEVisConstants.PrimitiveType.BOOLEAN:
                return "Boolean";
            default:
                return "Unknown";
        }

    }

    public String getOtherClass() {
        return otherClass.get();
    }

    public void setOtherClass(String fName) {
        otherClass.set(fName);
    }

    public String getType() {
        return type.get();
    }

    public void setType(String fName) {
        type.set(fName);
    }

    public String getDirection() {
        return direction.get();
    }

    public void setDirection(String fName) {
        direction.set(fName);
    }
}
