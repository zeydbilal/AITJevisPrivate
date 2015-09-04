/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.jeconfig.tool.ImageConverter;

/**
 *
 * @author CalisZ
 */
public class ManualWizardStep2 extends WizardPane {

    private JEVisObject parentObject;
    private JEVisClass createClass;
    private ObservableList<String> typeNames = FXCollections.observableArrayList();

    public ManualWizardStep2(JEVisObject parentObject) {
        setParentObject(parentObject);
        setMinSize(500, 500);
        setContent(getInit());
        setGraphic(null);
    }

    @Override
    public void onEnteringPage(Wizard wizard) {

        ObservableList<ButtonType> list = getButtonTypes();

        for (ButtonType type : list) {
            if (type.getButtonData().equals(ButtonBar.ButtonData.BACK_PREVIOUS)) {
                Node prev = lookupButton(type);
                prev.visibleProperty().setValue(Boolean.FALSE);
            }
        }
    }

    //TODO
    private BorderPane getInit() {
        BorderPane root = new BorderPane();

        ObservableList<JEVisClass> options = FXCollections.observableArrayList();

        try {
            options = FXCollections.observableArrayList(getParentObject().getAllowedChildrenClasses());
        } catch (JEVisException ex) {
            Logger.getLogger(ManualWizardStep2.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Set the cell properties for ComboBox
        Callback<ListView<JEVisClass>, ListCell<JEVisClass>> cellFactory = new Callback<ListView<JEVisClass>, ListCell<JEVisClass>>() {
            @Override
            public ListCell<JEVisClass> call(ListView<JEVisClass> param) {
                final ListCell<JEVisClass> cell = new ListCell<JEVisClass>() {
                    {
                        super.setPrefWidth(260);
                    }

                    @Override
                    public void updateItem(JEVisClass item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            HBox box = new HBox(5);
                            box.setAlignment(Pos.CENTER_LEFT);
                            try {
                                ImageView icon = ImageConverter.convertToImageView(item.getIcon(), 15, 15);
                                Label cName = new Label(item.getName());
                                cName.setTextFill(Color.BLACK);
                                box.getChildren().setAll(icon, cName);

                            } catch (JEVisException ex) {
                                Logger.getLogger(CreateTable.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            setGraphic(box);

                        }
                    }
                };
                return cell;
            }
        };

        Label serverName = new Label();

        ComboBox<JEVisClass> classComboBox = new ComboBox<JEVisClass>(options);
        classComboBox.setCellFactory(cellFactory);
        classComboBox.setButtonCell(cellFactory.call(null));
        classComboBox.setMinWidth(250);
        classComboBox.getSelectionModel().selectFirst();
        createClass = classComboBox.getSelectionModel().getSelectedItem();

        classComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                typeNames.clear();
                createClass = classComboBox.getSelectionModel().getSelectedItem();
                try {
                    for (int i = 0; i < createClass.getTypes().size(); i++) {
                        typeNames.add(createClass.getTypes().get(i).getName());

                        System.out.println(createClass.getTypes().get(i).getName() + " : " + createClass.getTypes().get(i).getGUIDisplayType());
                    }
                } catch (JEVisException ex) {
                    Logger.getLogger(CreateTable.class.getName()).log(Level.SEVERE, null, ex);
                }

                root.setCenter(getAttributNames());
            }
        });

        //Get and set the typenames from a class. Typenames are for the columnnames.
        try {
            for (int i = 0; i < createClass.getTypes().size(); i++) {
                typeNames.add(createClass.getTypes().get(i).getName());
            }
        } catch (JEVisException ex) {
            Logger.getLogger(CreateTable.class.getName()).log(Level.SEVERE, null, ex);
        }

        serverName.setText("Name : " + getParentObject().getName());

        //Servername and ComboBox
        HBox hBoxTop = new HBox();
        hBoxTop.setSpacing(10);
        hBoxTop.getChildren().addAll(serverName, classComboBox);
        hBoxTop.setPadding(new Insets(10, 10, 10, 10));

        root.setTop(hBoxTop);
        root.setCenter(getAttributNames());
        return root;
    }

    //Attribute names 
//    public VBox getAttributNames() {
//        Label indexTypeNames = new Label();
//
//        String str = "";
//        for (int i = 0; i < typeNames.size(); i++) {
//            str += typeNames.get(i) + "\n";
//            indexTypeNames.setText(str);
//        }
//
//        VBox vBoxCenter = new VBox();
//        vBoxCenter.setSpacing(10);
//        vBoxCenter.getChildren().addAll(indexTypeNames);
//        vBoxCenter.setPadding(new Insets(10, 10, 10, 10));
//
//        return vBoxCenter;
//    }
    public GridPane getAttributNames() {
        GridPane gridpane = new GridPane();

        for (int i = 0; i < typeNames.size(); i++) {
            Label label = new Label(typeNames.get(i) + " : ");
            try {
                System.out.println(createClass.getTypes().get(i).getName() + " : " + createClass.getTypes().get(i).getGUIDisplayType());
                if (createClass.getTypes().get(i).getGUIDisplayType() == null || createClass.getTypes().get(i).getGUIDisplayType().equals("Text")) {
                    TextField textField = new TextField();
                    textField.setPrefWidth(400);
                    gridpane.addRow(i, label, textField);
                } else {
                    CheckBox checkBox = new CheckBox();
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

    public JEVisObject getParentObject() {
        return this.parentObject;
    }

    public void setParentObject(JEVisObject parentObject) {
        this.parentObject = parentObject;
    }
}
