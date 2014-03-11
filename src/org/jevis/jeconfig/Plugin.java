/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig;

import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import org.jevis.jeapi.JEVisDataSource;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public interface Plugin {

    public String getName();

    public void setName(String name);

    public StringProperty nameProperty();

    public String getUUID();

    public void setUUID(String id);

    public StringProperty uuidProperty();

    Node getMenu();

    Node getToolbar();

    JEVisDataSource getDataSource();

    void setDataSource(JEVisDataSource ds);

//    void handelRequest(Command command);
    void handelRequest(int cmdType);

    Node getConntentNode();

    ImageView getIcon();

    void fireCloseEvent();
}
