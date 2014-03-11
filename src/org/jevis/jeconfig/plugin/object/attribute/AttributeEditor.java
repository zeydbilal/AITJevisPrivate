/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.object.attribute;

import javafx.scene.Node;
import org.jevis.jeapi.JEVisAttribute;
import org.jevis.jeapi.JEVisException;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public interface AttributeEditor {

    boolean hasChanged();

//    void setAttribute(JEVisAttribute att);
    void commit() throws JEVisException;

    Node getEditor();
}
