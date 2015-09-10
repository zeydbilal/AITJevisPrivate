/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.jeconfig.plugin.object.ObjectTree;

/**
 *
 * @author CalisZ
 */
public class ManualWizardStep3 extends WizardPane {

    private WizardSelectedObject wizardSelectedObject;
    private TextField csvFileNameTextField;
    private JEVisClass createClass;
    private ObjectTree tree;
    private ObservableList<String> typeNames = FXCollections.observableArrayList();
    private ObservableList<String> listBuildSample = FXCollections.observableArrayList();
    private Map<String, String> map = new TreeMap<String, String>();

    public ManualWizardStep3(ObjectTree tree, WizardSelectedObject wizardSelectedObject) {
        this.wizardSelectedObject = wizardSelectedObject;
        this.tree = tree;
        setMinSize(500, 500);

        setGraphic(null);
    }

    @Override
    public void onEnteringPage(Wizard wizard) {
        setContent(getInit());
        ObservableList<ButtonType> list = getButtonTypes();

        for (ButtonType type : list) {
            if (type.getButtonData().equals(ButtonBar.ButtonData.BACK_PREVIOUS)) {
                Node prev = lookupButton(type);
                prev.visibleProperty().setValue(Boolean.FALSE);

            }
        }
    }

    @Override
    public void onExitingPage(Wizard wizard) {
        //TODO
        System.out.println("wizardSelectedObject lezte objekt : " + wizardSelectedObject.getSelectedObject().getName());

    }

    private BorderPane getInit() {
        BorderPane root = new BorderPane();

        Label serverName = new Label("File Name : ");
        csvFileNameTextField = new TextField();
        csvFileNameTextField.setPrefWidth(200);
        csvFileNameTextField.setPromptText("Server Name");

        //File Name
        HBox hBoxTop = new HBox();
        hBoxTop.setSpacing(10);
        hBoxTop.getChildren().addAll(serverName, csvFileNameTextField);
        hBoxTop.setPadding(new Insets(10, 10, 10, 10));

        //TODO initialisiere createClass 
        ObservableList<JEVisClass> childrenList = FXCollections.observableArrayList();

        try {
            childrenList = FXCollections.observableArrayList(wizardSelectedObject.getSelectedObject().getAllowedChildrenClasses());
        } catch (JEVisException ex) {
            Logger.getLogger(ManualWizardStep2.class.getName()).log(Level.SEVERE, null, ex);
        }
        //TODO 
        for (JEVisClass child : childrenList) {
            
        }

//createClass = childrenList.
        root.setTop(hBoxTop);
        root.setCenter(getTypes());

        return root;
    }

    //Erzeuge Label,TextField und CheckBox variablen von schon definierter Typen.
    public GridPane getTypes() {
        GridPane gridpane = new GridPane();

        for (int i = 0; i < typeNames.size(); i++) {
            Label label = new Label(typeNames.get(i) + " : ");
            try {
                if (createClass.getTypes().get(i).getGUIDisplayType() == null || createClass.getTypes().get(i).getGUIDisplayType().equals("Text")) {

                    TextField textField = new TextField();
                    textField.setId(createClass.getTypes().get(i).getName());
                    textField.setPrefWidth(400);
                    textField.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            map.put(textField.getId(), textField.getText());
                        }
                    });
                    gridpane.addRow(i, label, textField);
                } else {
                    CheckBox checkBox = new CheckBox();

                    checkBox.setId(createClass.getTypes().get(i).getName());
                    checkBox.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            if (checkBox.isSelected() == true) {
                                map.put(checkBox.getId(), "1");
                            } else {
                                map.put(checkBox.getId(), "0");
                            }
                        }
                    });

                    gridpane.addRow(i, label, checkBox);
                }
            } catch (JEVisException ex) {
                Logger.getLogger(ManualWizardStep2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        gridpane.setHgap(10);//horizontal gap in pixels 
        gridpane.setVgap(10);//vertical gap in pixels
        gridpane.setPadding(new Insets(10, 10, 10, 10));////margins around the whole grid

        return gridpane;
    }
}
