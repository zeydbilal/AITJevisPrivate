/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.classes;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.jevis.jeapi.JEVisClass;
import org.jevis.jeapi.JEVisClassRelationship;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisRelationship;
import org.jevis.jeapi.JEVisConstants.*;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class RelationshipColum {

    private SimpleStringProperty otherClass = new SimpleStringProperty("Error");
    private SimpleStringProperty type = new SimpleStringProperty("Error");
    private SimpleStringProperty direction = new SimpleStringProperty("Error");

    /**
     *
     * @param relation
     * @param jclass
     */
    public RelationshipColum(JEVisClassRelationship relation, JEVisClass jclass) {
        try {
            this.otherClass = new SimpleStringProperty(relation.getOtherClass(jclass).getName());
            this.type = new SimpleStringProperty(getTypeName(relation.getType()));
            if (relation.getStart().getName().equals(jclass.getName())) {
                this.direction = new SimpleStringProperty(getDirectionName(Direction.FORWARD));
            } else {
                this.direction = new SimpleStringProperty(getDirectionName(Direction.BACKWARD));
            }
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
            case ClassRelationship.OK_PARENT:
                return "Vaild Parent";
            case ClassRelationship.INHERIT:
                return "Inhereted";
            case ClassRelationship.NESTED:
                return "Nested";
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
