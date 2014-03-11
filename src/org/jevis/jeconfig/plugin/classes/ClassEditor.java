/**
 * Copyright (C) 2014 Envidatec GmbH <info@envidatec.com>
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
 */
package org.jevis.jeconfig.plugin.classes;

import java.awt.Desktop;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.jevis.jeapi.JEVisClass;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.tool.ImageConverter;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ClassEditor {

    private Desktop desktop = Desktop.getDesktop();
    private JEVisClass _class;
    Button fIcon;
    private boolean _typeOpen = false;
    private boolean _relationOpen = false;

    public ClassEditor() {
    }

    public Node buildEditor(JEVisClass jclass) {
        _class = jclass;

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5, 0, 20, 20));
        gridPane.setHgap(7);
        gridPane.setVgap(7);

        Label lName = new Label("Name:");
        Label lDescription = new Label("Description:");
        Label lIsUnique = new Label("Unique:");
        Label lIcon = new Label("Icon:");
        Label lRel = new Label("Relaionships:");
        Label lInherit = new Label("Inheritance");
        Label lTypes = new Label("Types:");


        TextField fName = new TextField();
        fName.prefWidthProperty().set(250d);
        TextArea fDescript = new TextArea();
        fIcon = new Button("", getIcon(jclass));
        CheckBox fUnique = new CheckBox();
        fUnique.setSelected(false);

        ClassRelationshipTable table = new ClassRelationshipTable();
        GridPane tTable = table.buildTree(jclass);

        TypeTable typeTable = new TypeTable();
        Button fInherit = new Button();

        gridPane.add(lName, 0, 0);
        gridPane.add(fName, 1, 0);
        gridPane.add(lInherit, 0, 1);
        gridPane.add(fInherit, 1, 1);
        gridPane.add(lIcon, 0, 2);
        gridPane.add(fIcon, 1, 2);
        gridPane.add(lIsUnique, 0, 3);
        gridPane.add(fUnique, 1, 3);
        gridPane.add(lDescription, 0, 4);
        gridPane.add(fDescript, 1, 4, 2, 1);
//        gridPane.add(lRel, 0, 5);
//        gridPane.add(tTable, 1, 5);
//        gridPane.add(lTypes, 0, 6);
//        gridPane.add(typeTable.buildTree(jclass), 1, 6);


        GridPane.setHalignment(lInherit, HPos.RIGHT);
        GridPane.setHalignment(lIcon, HPos.RIGHT);
        GridPane.setValignment(lIcon, VPos.TOP);
        GridPane.setHalignment(lName, HPos.RIGHT);
        GridPane.setHalignment(lIsUnique, HPos.RIGHT);
        GridPane.setHalignment(lDescription, HPos.RIGHT);
        GridPane.setValignment(lDescription, VPos.TOP);
        GridPane.setHalignment(lRel, HPos.RIGHT);
        GridPane.setValignment(lRel, VPos.TOP);
        GridPane.setHgrow(tTable, Priority.ALWAYS);
        GridPane.setHalignment(lTypes, HPos.RIGHT);
        GridPane.setValignment(lTypes, VPos.TOP);

        try {
            if (jclass != null) {
                fName.setText(jclass.getName());
                if (jclass.getInheritance() != null) {
                    fInherit.setText(jclass.getInheritance().getName());
                } else {
                    fInherit.setText("");
                }

                fDescript.setText(jclass.getDescription());
                fUnique.setSelected(jclass.isUnique());
            }

        } catch (JEVisException ex) {
            Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);
        }


        fIcon.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                FileChooser fileChooser = new FileChooser();
                if (JEConfig.getLastFile() != null) {
                    fileChooser.setInitialDirectory(JEConfig.getLastFile().getParentFile());
                }

                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
                FileChooser.ExtensionFilter gifFilter = new FileChooser.ExtensionFilter("GIF files (*.gif)", "*.gif");
                FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
                fileChooser.getExtensionFilters().addAll(extFilter, gifFilter, jpgFilter);
                File file = fileChooser.showOpenDialog(JEConfig.getStage());
                if (file != null) {
                    openFile(file);
                    JEConfig.setLastFile(file);
                }
            }
        });

//        Group root = new Group();
        final TitledPane t1 = new TitledPane("General", gridPane);
        TitledPane t2 = new TitledPane("Types", typeTable.buildTree(jclass));
        TitledPane t3 = new TitledPane("Relationships", tTable);
        t1.setAnimated(false);
        t1.setExpanded(true);
        t2.setExpanded(_typeOpen);
        t3.setExpanded(_relationOpen);

        t1.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                t1.setAnimated(true);
            }
        });

        t2.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                _typeOpen = newValue;
            }
        });
        t3.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                _relationOpen = newValue;
            }
        });


//        t1.setStyle("-fx-background-color: rgba(0, 100, 100, 0);");

//        Accordion accordion = new Accordion();
//        accordion.getPanes().add(t1);
//        accordion.getPanes().add(t2);
//        accordion.getPanes().add(t3);
//        root.getChildren().add(accordion);
        VBox root = new VBox();
        root.getChildren().addAll(t1, t2, t3);

        return root;

//        TabPane tabPane = new TabPane();
//        Tab t1 = new Tab("General");
//        t1.setContent(gridPane);
//        Tab t2 = new Tab("Types");
//        t2.setContent(tTable);
//        Tab t3 = new Tab("Relationships");
//        t3.setContent(typeTable.buildTree(jclass));
//
//        tabPane.getTabs().addAll(t1, t2, t3);


//        return tabPane;

//        return gridPane;
    }

    private void openFile(File file) {
        try {
//            Image image = new Image(file.toURI().toString());
//            ImageView iv = new ImageView(image);
//            _class.setIcon(new ImageIcon(convertToAwtImage(image).getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH)));
            _class.setIcon(file);
//            File newIcon = desktop.open(file);
            _class.commit();
//            fIcon.setGraphic(getImageView(_class));

        } catch (Exception ex) {
            Logger.getLogger(ClassEditor.class.getName()).log(Level.SEVERE, null, ex);
            Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);
        }
    }

    public void comitAll() {
        try {
            _class.commit();
        } catch (JEVisException ex) {
            Logger.getLogger(ClassEditor.class.getName()).log(Level.SEVERE, null, ex);
            Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);
        }
    }

    private ImageView getIcon(JEVisClass jclass) {
        try {
            return ImageConverter.convertToImageView(jclass.getIcon(), 30, 30);
        } catch (Exception ex) {
            System.out.println("Error while geeting class icon: " + ex);
            ex.printStackTrace();
            return JEConfig.getImage("1393615831_unknown2.png", 30, 30);
        }


//        try{
////            IconView = new IconView(jclass.getIcon());
//            
//        }catch(Exception ex){
//            
//        }
    }

    private Node buildProerties() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5, 0, 20, 20));
        gridPane.setHgap(7);
        gridPane.setVgap(7);

        return gridPane;
    }

    private Node buildRelationships() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5, 0, 20, 20));
        gridPane.setHgap(7);
        gridPane.setVgap(7);

        return gridPane;
    }
}
