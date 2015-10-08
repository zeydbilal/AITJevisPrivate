/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import java.util.Comparator;
import java.util.List;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.jeconfig.plugin.object.ObjectTree;
import org.jevis.jeconfig.tool.ImageConverter;
import org.joda.time.DateTime;

/**
 *
 * @author Zeyd Bilal Calis
 */
//In dieser Klasse wird ein Server Objekt erzeugt und seine Attribute werden in die datenbank abgespeichert.
public class AutomatedWizardStep2 extends WizardPane {

    private JEVisClass createClass;
    private TextField serverNameTextField;
    private TextField databaseNameTextField;
    private ObjectTree tree;
    private ObservableList<String> typeNames = FXCollections.observableArrayList();
    private ObservableList<String> listBuildSample = FXCollections.observableArrayList();
    private WizardSelectedObject wizardSelectedObject;
    private Map<String, String> map = new TreeMap<String, String>();
    //Hier wird die Verbindungsdaten(Datanbankname,Host,Port) für den HTTP-Server initialisiert.
    private SensorMap sensorMap;

    public AutomatedWizardStep2(ObjectTree tree, WizardSelectedObject wizardSelectedObject, SensorMap sensorMap) {
        this.sensorMap = sensorMap;
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
        //Erzeuge das Server-Objekt
        commitServerObject();
    }

    public void commitServerObject() {
        try {
            //Create Server.
            JEVisObject newObject = wizardSelectedObject.getCurrentSelectedObject().buildObject(serverNameTextField.getText(), createClass);
            newObject.commit();
            //Commit the Attributes
            commitAttributes(newObject);

            //wähle den Server als neue Objekt!
            wizardSelectedObject.setCurrentSelectedObject(newObject);
            //Set the database name 
            sensorMap.setDatabase(databaseNameTextField.getText());
        } catch (JEVisException ex) {
            Logger.getLogger(AutomatedWizardStep2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void commitAttributes(JEVisObject newObject) {
        try {
            List<JEVisAttribute> attribut = newObject.getAttributes();
            ObservableList<JEVisAttribute> mylist = FXCollections.observableArrayList(attribut);
            sortTheChildren(mylist);

            for (Map.Entry<String, String> entrySet : map.entrySet()) {
                listBuildSample.add(entrySet.getValue());
                // Set the host
                if (entrySet.getKey().equals("Host")) {
                    sensorMap.setUrl(entrySet.getValue());
                    // Set the port
                } else if (entrySet.getKey().equals("Port")) {
                    sensorMap.setPort(entrySet.getValue());
                }
            }
            //Speichere die Samples in die Datenbank ab.
            for (int i = 0; i < mylist.size(); i++) {
                if (!listBuildSample.get(i).isEmpty()) {
                    mylist.get(i).buildSample(new DateTime(), listBuildSample.get(i)).commit();
                }
            }

        } catch (JEVisException ex) {
            Logger.getLogger(AutomatedWizardStep2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sortTheChildren(ObservableList<JEVisAttribute> list) {
        Comparator<JEVisAttribute> sort = new Comparator<JEVisAttribute>() {
            @Override
            public int compare(JEVisAttribute o1, JEVisAttribute o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        FXCollections.sort(list, sort);
    }

    // GUI Elemente
    private BorderPane getInit() {
        BorderPane root = new BorderPane();

        ObservableList<JEVisClass> options = FXCollections.observableArrayList();

        try {
            options = FXCollections.observableArrayList(wizardSelectedObject.getCurrentSelectedObject().getAllowedChildrenClasses());
        } catch (JEVisException ex) {
            Logger.getLogger(AutomatedWizardStep2.class.getName()).log(Level.SEVERE, null, ex);
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
        serverNameTextField = new TextField();
        serverNameTextField.setPrefWidth(200);
        serverNameTextField.setPromptText("Server Name");

        Label databaseName = new Label();
        databaseNameTextField = new TextField();
        databaseNameTextField.setPrefWidth(200);
        databaseNameTextField.setPromptText("Datenbank Name");

        ComboBox<JEVisClass> classComboBox = new ComboBox<JEVisClass>();

        for (JEVisClass option : options) {
            try {
                //Add only HTTP Server in to the ComboBox
                if (option.getName().equals("HTTP Server")) {
                    classComboBox.getItems().add(option);
                }
            } catch (JEVisException ex) {
                Logger.getLogger(AutomatedWizardStep2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        classComboBox.setCellFactory(cellFactory);
        classComboBox.setButtonCell(cellFactory.call(null));
        classComboBox.setMinWidth(250);
        classComboBox.getSelectionModel().selectFirst();

        createClass = classComboBox.getSelectionModel().getSelectedItem();

        classComboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                typeNames.clear();
                map.clear();
                listBuildSample.clear();
                createClass = classComboBox.getSelectionModel().getSelectedItem();
                try {
                    for (int i = 0; i < createClass.getTypes().size(); i++) {
                        typeNames.add(createClass.getTypes().get(i).getName());
                    }
                } catch (JEVisException ex) {
                    Logger.getLogger(CreateTable.class.getName()).log(Level.SEVERE, null, ex);
                }
                root.setCenter(getTypes());
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

        serverName.setText("Server Name : ");
        databaseName.setText("Database Name : ");
        //Servername and ComboBox
        HBox hBoxTopServerName = new HBox();
        hBoxTopServerName.setSpacing(30);
        hBoxTopServerName.getChildren().addAll(serverName, serverNameTextField, classComboBox);
        hBoxTopServerName.setPadding(new Insets(10, 10, 10, 0));

        HBox hBoxTopDatabaseName = new HBox();
        hBoxTopDatabaseName.setSpacing(10);
        hBoxTopDatabaseName.getChildren().addAll(databaseName, databaseNameTextField);
        hBoxTopDatabaseName.setPadding(new Insets(10, 10, 10, 0));

        VBox vBoxTop = new VBox();
        vBoxTop.getChildren().addAll(hBoxTopServerName, hBoxTopDatabaseName);
        vBoxTop.setPadding(new Insets(10, 10, 10, 10));
        root.setTop(vBoxTop);
        root.setCenter(getTypes());

        return root;

    }

    //Erzeuge Label,TextField und CheckBox variablen von schon vordefinierten Typen.
    public GridPane getTypes() {
        GridPane gridpane = new GridPane();

        for (int i = 0; i < typeNames.size(); i++) {
            Label label = new Label(typeNames.get(i) + " : ");
            try {
                if (createClass.getTypes().get(i).getGUIDisplayType() == null || createClass.getTypes().get(i).getGUIDisplayType().equals("Text")) {

                    TextField textField = new TextField();
                    textField.setId(createClass.getTypes().get(i).getName());
                    textField.setPrefWidth(400);

                    map.put(textField.getId(), textField.getText());

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
                    checkBox.setSelected(false);

                    if (checkBox.isSelected() == true) {
                        map.put(checkBox.getId(), "1");
                    } else {
                        map.put(checkBox.getId(), "0");
                    }

                    map.put(checkBox.getId(), checkBox.getText());

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
                Logger.getLogger(AutomatedWizardStep2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        gridpane.setHgap(10);//horizontal gap in pixels 
        gridpane.setVgap(10);//vertical gap in pixels
        gridpane.setPadding(new Insets(10, 10, 10, 10));////margins around the whole grid

        return gridpane;
    }
}
