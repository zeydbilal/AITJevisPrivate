/**
 * Copyright (C) 2009 - 2014 Envidatec GmbH <info@envidatec.com>
 *
 * This file is part of JEConfig.
 *
 * JEConfig is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation in version 3.
 *
 * JEConfig is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * JEConfig. If not, see <http://www.gnu.org/licenses/>.
 *
 * JEConfig is part of the OpenJEVis project, further project information are
 * published at <http://www.OpenJEVis.org/>.
 */
package org.jevis.jeconfig.plugin.classes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPaneBuilder;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisObject;
import org.jevis.jeconfig.Constants;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.Plugin;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ClassPlugin implements Plugin {

    private StringProperty name = new SimpleStringProperty("*NO_NAME*");
    private StringProperty id = new SimpleStringProperty("*NO_ID*");
    private JEVisDataSource ds;
    private ClassEditor editor;
    private ClassTree tf = new ClassTree();

    public ClassPlugin(JEVisDataSource ds, String newname) {
        this.ds = ds;
        name.set(newname);
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public void setName(String value) {
        name.set(value);
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public String getUUID() {
        return id.get();
    }

    @Override
    public void setUUID(String newid) {
        id.set(newid);
    }

    @Override
    public StringProperty uuidProperty() {
        return id;
    }

    @Override
    public Node getConntentNode() {

        VBox editorPane = new VBox();
        editorPane.setId("objecteditorpane");
//        System.out.println("1");
        TreeView<JEVisObject> tree = tf.SimpleTreeView(ds, editorPane);
//        System.out.println("2");

        SplitPane sp = SplitPaneBuilder.create()
                .items(tree, editorPane)
                .dividerPositions(new double[]{.2d, 0.8d}) // why does this not work!?
                .orientation(Orientation.HORIZONTAL)
                .build();
        sp.setId("mainsplitpane");

        return sp;
    }

    @Override
    public Node getMenu() {
        return null;
    }

    @Override
    public Node getToolbar() {
        return null;
    }

    @Override
    public JEVisDataSource getDataSource() {
        return ds;
    }

    @Override
    public void setDataSource(JEVisDataSource ds) {
        this.ds = ds;
    }

//    @Override
//    public void handelRequest(Command command) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
    @Override
    public void handelRequest(int cmdType) {
        System.out.println("Classplugin command handler: " + cmdType);
        try {
            switch (cmdType) {
                case Constants.Plugin.Command.SAVE:
                    System.out.println("speichern");
//                    editor.comitAll();
                    tf.fireSave();
                    break;
                case Constants.Plugin.Command.DELTE:
//                    tf.fireDelete();
                    break;
                case Constants.Plugin.Command.EXPAND:
//                    System.out.println("Expand");
                    break;
                case Constants.Plugin.Command.NEW:
//                    tf.fireEventNew();
                    break;
                default:
                    System.out.println("Unknows command ignore...");
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public ImageView getIcon() {
        return JEConfig.getImage("1394482166_blueprint_tool.png", 20, 20);
    }

    @Override
    public void fireCloseEvent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
