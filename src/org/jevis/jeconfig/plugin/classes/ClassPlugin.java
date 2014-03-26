/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import org.jevis.jeapi.JEVisDataSource;
import org.jevis.jeapi.JEVisObject;
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
        TreeView<JEVisObject> tree = tf.SimpleTreeView(ds, editorPane);

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
        try {
            switch (cmdType) {
                case Constants.Plugin.Command.SAVE:
                    System.out.println("speichern");
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
