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
 *
 * JEConfig is part of the OpenJEVis project, further project information are
 * published at <http://www.OpenJEVis.org/>.
 */
package org.jevis.jeconfig.plugin.unit;

import com.sun.org.omg.CORBA.AttributeMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javax.measure.unit.Unit;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisException;
import static org.jevis.application.dialog.AboutDialog.ICON_TASKBAR;
import org.jevis.application.dialog.DialogHeader;
import org.jevis.application.resource.ResourceLoader;
import org.jevis.commons.unit.UnitManager;
import org.jevis.jeconfig.JEConfig;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class SimpleUnitChooser {

    public static enum Response {

        NO, YES, CANCEL
    };

    private Response response = Response.CANCEL;

    UnitTree uTree;

    private Label exampleFiled = new Label();
    private Unit _unit;
    private String _prefix;
    private ComboBox unitsBox;
    private ComboBox<String> prefixBox;
    private boolean isDefault = false;
    private JEVisAttribute _att;

    public SimpleUnitChooser() {
    }

    public Response show(Point2D position, String title, final JEVisAttribute att) throws JEVisException {
        final Stage stage = new Stage();
        _att = att;

        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
//        stage.initOwner(owner);
        stage.setX(position.getX());
        stage.setY(position.getY());
        stage.setWidth(280);
        stage.setHeight(240);

        VBox root = new VBox();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        //TODo better be dynamic

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(true);

        ImageView imageView = ResourceLoader.getImage(ICON_TASKBAR, 65, 65);

        Node header = DialogHeader.getDialogHeader("1404313956_evolution-tasks.png", "Unit Selection");

        stage.getIcons().add(imageView.getImage());

//        Node header = DialogHeader.getDialogHeader("1404313956_evolution-tasks.png", "Unit Selection");
        HBox buttonPanel = new HBox();

        Button ok = new Button("OK");
        ok.setDefaultButton(true);

        Button cancel = new Button("Cancel");
        cancel.setCancelButton(true);

        Button changeBaseUnit = new Button();//new Button("Basic Unit");
        changeBaseUnit.setGraphic(JEConfig.getImage("1394482640_package_settings.png", 17, 17));
        CheckBox keepDefault = new CheckBox("Set as default");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        buttonPanel.getChildren().addAll(keepDefault, spacer, ok, cancel);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));
        buttonPanel.setSpacing(10);
        buttonPanel.setMaxHeight(25);

//        uTree = new UnitTree(att.getDataSource());
//        uTree.setPrefSize(550, 600);
        Label prefixL = new Label("Prefix:");
        Label unitL = new Label("Unit:");
        Label example = new Label("Symbol: ");

        if (att.getType().getUnit() != null) {
            Unit unit = att.getType().getUnit();
            setBaseUnit(unit);
        } else {
            System.out.println("Unit is emty");
        }

//
        HBox unitBox = new HBox(5);
        unitBox.getChildren().setAll(unitsBox, changeBaseUnit);
        unitBox.setMaxWidth(520);

//        VBox box = new VBox();
//        box.getChildren().add(uTree);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        grid.add(prefixL, 0, 0);
        grid.add(unitL, 0, 1);
        grid.add(example, 0, 2);

        grid.add(prefixBox, 1, 0);
        grid.add(unitBox, 1, 1);
//        grid.add(changeBaseUnit, 1, 2);
        grid.add(exampleFiled, 1, 2);
//
//        GridPane.setHgrow(prefixBox, Priority.ALWAYS);
//        GridPane.setHgrow(unitBox, Priority.ALWAYS);

        //spalte , zeile bei 0 starten
//        grid.add(uTree, 0, 0);
        root.getChildren().setAll(header, new Separator(Orientation.HORIZONTAL), grid, buttonPanel);
//        VBox.setVgrow(box, Priority.ALWAYS);

        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
//                System.out.println("Size: h:" + stage.getHeight() + " w:" + stage.getWidth());
                response = Response.YES;
                saveSettings();
                stage.close();
//                isOK.setValue(true);

            }
        });

        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                response = Response.CANCEL;
                stage.close();

            }
        });

        prefixBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                setPrefix(t1);
            }
        });
        keepDefault.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                isDefault = t1;
            }
        });

//        prefixBox.setPrefWidth(120);
        prefixBox.setMaxWidth(520);
//        
        changeBaseUnit.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                UnitSelectDialog usd = new UnitSelectDialog();
                try {
                    if (usd.show(JEConfig.getStage(), "Select Basic Unit", att.getDataSource()) == UnitSelectDialog.Response.YES) {
                        System.out.println("ok new basic unit: " + usd.getUnit());
                        setBaseUnit(usd.getUnit());
                    }
                } catch (JEVisException ex) {
                    Logger.getLogger(SimpleUnitChooser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        stage.sizeToScene();
        stage.showAndWait();

        return response;
    }

    public void saveSettings() {
        try {
            if (_att.getUnit().equals(getUnit()) && isDefault()) {
                System.out.println("Diff unit set new unit -->" + getUnit());
                _att.setUnit(getUnit());

            }
        } catch (JEVisException ex) {
            Logger.getLogger(SimpleUnitChooser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isDefault() {
        return isDefault;
    }

    private void printExample() {
        System.out.println("prefix: " + getPrefix());
        exampleFiled.setText(getPrefix() + UnitManager.getInstance().formate(_unit));
    }

    private void setUnit(Unit unit) {
        _unit = unit;
        printExample();
    }

    public Unit getUnit() {
        return _unit;
    }

    public String getPrefix() {
        System.out.println("pp: " + _prefix);
        if (_prefix == null || _prefix.isEmpty()) {
            return "";
        } else {
            return UnitManager.getInstance().getPrefixChar(_prefix);
        }
    }

    private void setPrefix(String prefix) {
        System.out.println("setPrifix: " + prefix);
        _prefix = prefix;
        printExample();
    }

    private void setBaseUnit(final Unit unit) {
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {

        List<Unit> units = new ArrayList();
        if (unit != null) {
            System.out.println("Unit is not emty: " + unit);
            units = UnitManager.getInstance().getCompatibleSIUnit(unit);
            units.addAll(UnitManager.getInstance().getCompatibleNonSIUnit(unit));
            units.addAll(UnitManager.getInstance().getCompatibleAdditionalUnit(unit));
            units.add(unit);
        }

        unitsBox = buildUnitBox(units);
        unitsBox.getSelectionModel().select(unit);

        unitsBox.setPrefWidth(120);
        unitsBox.setMaxWidth(520);

        prefixBox = new ComboBox(FXCollections.observableArrayList(UnitManager.getInstance().getPrefixes()));
        prefixBox.getSelectionModel().select("");//toto get elsewhere?!
//            }
//        });

    }

    private ComboBox<Unit> buildUnitBox(List<Unit> units) {
        Callback<ListView<Unit>, ListCell<Unit>> cellFactory = new Callback<ListView<Unit>, ListCell<Unit>>() {
            @Override
            public ListCell<Unit> call(ListView<Unit> param) {
                final ListCell<Unit> cell = new ListCell<Unit>() {
                    {
                        super.setPrefWidth(60);

                    }

                    @Override
                    public void updateItem(Unit item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {

                            HBox box = new HBox(5);
                            box.setAlignment(Pos.CENTER_LEFT);

//                            Label name = new Label(item.toString());
                            Label name = new Label(UnitManager.getInstance().formate(item));
                            name.setTextFill(Color.BLACK);

                            box.getChildren().setAll(name);
                            setGraphic(box);

                            _unit = item;
                            setUnit(_unit);

                        }
                    }
                };
                return cell;
            }
        };

        ObservableList<Unit> options = FXCollections.observableArrayList(units);

        final ComboBox<Unit> comboBox = new ComboBox<Unit>(options);
        comboBox.setCellFactory(cellFactory);
//        comboBox.setButtonCell(cellFactory.call(null));
        comboBox.setButtonCell(new ListCell<Unit>() {

            @Override
            protected void updateItem(Unit t, boolean bln) {
                super.updateItem(t, bln); //To change body of generated methods, choose Tools | Templates.
                System.out.println("t: " + bln + "  " + t);
                if (t != null) {
                    setText(UnitManager.getInstance().formate(t));
                }

            }

        });
//        comboBox.setConverter(new StringConverter<Unit>() {
//
//            @Override
//            public String toString(Unit t) {
//                return UnitManager.getInstance().formate(t);
//            }
//
//            @Override
//            public Unit fromString(String string) {
//                return Unit.valueOf(string);
////                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
//        });

        //TODO: load default lange from Configfile or so
        comboBox.getSelectionModel().selectFirst();

        comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Unit>() {

            @Override
            public void changed(ObservableValue<? extends Unit> ov, Unit t, Unit t1) {
                setUnit(t1);
            }
        });

        comboBox.setMaxWidth(Integer.MAX_VALUE);//workaround

        return comboBox;

    }
}
