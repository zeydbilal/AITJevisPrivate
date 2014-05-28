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
package org.jevis.jeconfig.tool;

import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javax.measure.unit.Unit;
import org.jevis.api.JEVisType;
import org.jevis.commons.unit.UnitManager;

public class UnitChooserControler {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField altSymbol;

    @FXML
    private ComboBox<?> boxMeaning;

    @FXML
    private ComboBox<Object> boxPrefix;

    @FXML
    private ComboBox<Object> boxQuantity;

    @FXML
    private ComboBox<Object> boxUnit;

    @FXML
    private Label example;

    @FXML
    private TextField searchField;

    @FXML
    private GridPane gb;

    private final UnitManager um = UnitManager.getInstance();

    @FXML
    void initialize() {
        assert altSymbol != null : "fx:id=\"altPrefix\" was not injected: check your FXML file 'UnitChooser.fxml'.";
        assert boxMeaning != null : "fx:id=\"boxMeaning\" was not injected: check your FXML file 'UnitChooser.fxml'.";
        assert boxPrefix != null : "fx:id=\"boxPrefix\" was not injected: check your FXML file 'UnitChooser.fxml'.";
        assert boxQuantity != null : "fx:id=\"boxQuantity\" was not injected: check your FXML file 'UnitChooser.fxml'.";
        assert boxUnit != null : "fx:id=\"boxUnit\" was not injected: check your FXML file 'UnitChooser.fxml'.";
        assert example != null : "fx:id=\"example\" was not injected: check your FXML file 'UnitChooser.fxml'.";
        assert searchField != null : "fx:id=\"searchField\" was not injected: check your FXML file 'UnitChooser.fxml'.";
        assert gb != null : "fx:id=\"gb\" was not injected: check your FXML file 'UnitChooser.fxml'.";

        Tooltip tt = new Tooltip("Unit Prefix e.g. Kilo,Mega");
        boxPrefix.setTooltip(tt);

        fillQuantitys();
        fillPrifix();
    }

    class UnitListCell extends ListCell<Object> {

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            if (item instanceof Unit) {
                String label = String.format("%s [%s]", item.toString(), UnitManager.getInstance().getUnitName((Unit) item, Locale.ENGLISH));
                setText(label);
//                setText(UnitManager.getInstance().getQuantitiesName((Unit) item, Locale.getDefault()));
            }

        }
    }

    class QuantitiesListCell extends ListCell<Object> {

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            if (item instanceof Unit) {
                String label = String.format("%s", UnitManager.getInstance().getQuantitiesName((Unit) item, Locale.ENGLISH));
                setText(label);
//                setText(UnitManager.getInstance().getQuantitiesName((Unit) item, Locale.getDefault()));
            }

        }
    }

    @FXML
    void handelQuantityEvent(ActionEvent event) {
        Object obj = boxQuantity.getSelectionModel().getSelectedItem();
        if (obj instanceof Unit) {
            fillUnits((Unit) obj);
            printExample();
        } else {
            boxQuantity.getSelectionModel().selectPrevious();
        }

    }

    @FXML
    void handelMeaningAction(ActionEvent event) {
    }

    @FXML
    void handleAltSymbolAction(KeyEvent event) {
        printExample();
    }

    @FXML
    void handlePrefixAction(ActionEvent event) {
        printExample();
    }

    @FXML
    void handleSearchAction(ActionEvent event) {
        searchUnit();
    }

    @FXML
    void handleSearchKeyAction(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchUnit();
        }
    }

    @FXML
    void handleUnitAction(ActionEvent event) {
        printExample();

    }

    public void setUnit(JEVisType type) {
        try {
            boxQuantity.getSelectionModel().select(type.getUnit().getStandardUnit());
            boxUnit.getSelectionModel().select(type.getUnit().getStandardUnit());
            //TODO set Prefix, maybe we need to store this also seperate?
            altSymbol.setText(type.getAlternativSymbol());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void fillQuantitys() {
        boxQuantity.setButtonCell(new QuantitiesListCell());
        boxQuantity.setCellFactory(new Callback<ListView<Object>, ListCell<Object>>() {
            @Override
            public ListCell<Object> call(ListView<Object> p) {
                return new QuantitiesListCell();
            }
        });

        ObservableList<Unit> favList = FXCollections.observableList(um.getFavoriteQuantitys());
        ObservableList<Unit> allList = FXCollections.observableList(um.getQuantities());

//        gb.getChildren().remove(boxQuantity);
//        boxQuantity = new ComboBox<>();
//        gb.getChildren().add(boxQuantity);
        boxQuantity.getItems().clear();
        boxQuantity.getSelectionModel().clearSelection();
        boxQuantity.getItems().addAll(favList);
        boxQuantity.getItems().addAll(new Separator());
        boxQuantity.getItems().addAll(allList);

        boxQuantity.getSelectionModel().selectFirst();

    }

    private void fillUnits(Unit unit) {
        boxUnit.setButtonCell(new UnitListCell());
        boxUnit.setCellFactory(new Callback<ListView<Object>, ListCell<Object>>() {
            @Override
            public ListCell<Object> call(ListView<Object> p) {
                return new UnitListCell();
            }
        });

        ObservableList<Unit> siList = FXCollections.observableList(um.getCompatibleSIUnit(unit));
        ObservableList<Unit> nonSIList = FXCollections.observableList(um.getCompatibleNonSIUnit(unit));
        ObservableList<Unit> addList = FXCollections.observableList(um.getCompatibleAdditionalUnit(unit));

        boxUnit.getItems().clear();
        boxUnit.getSelectionModel().clearSelection();
        boxUnit.getItems().add(unit);
        boxUnit.getItems().addAll(siList);
        boxQuantity.getItems().addAll(new Separator());
        boxUnit.getItems().addAll(nonSIList);
        boxQuantity.getItems().addAll(new Separator());
        boxUnit.getItems().addAll(addList);

        boxUnit.getSelectionModel().selectFirst();

    }

    private void fillPrifix() {
        ObservableList<String> list = FXCollections.observableList(um.getPrefixes());

        boxPrefix.getItems().clear();
        boxPrefix.getSelectionModel().clearSelection();
        boxPrefix.getItems().addAll(list);
        boxPrefix.getSelectionModel().selectFirst();//noUnit
    }

    public String getAltSymbol() {
        return altSymbol.getText();
    }

    public Unit getFinalUnit() {
        Unit baseUnit = null;
        Unit altUnit = null;
        String prefixUnit = null;

        //check Unit
        Object baseUnitObj = boxUnit.getSelectionModel().getSelectedItem();
        if (baseUnitObj instanceof Unit) {
            baseUnit = (Unit) baseUnitObj;
        }

        //check Prefix
        Object prefixObj = boxPrefix.getSelectionModel().getSelectedItem();
        if (boxPrefix.getSelectionModel().getSelectedIndex() != 0 && prefixObj instanceof String && baseUnit != null) {
            prefixUnit = (String) prefixObj;
            // 
        }

        //check altSymbol
        if (!altSymbol.getText().isEmpty() && baseUnit != null) {
            try {
                altUnit = baseUnit.alternate(altSymbol.getText());
                altSymbol.setStyle("-fx-text-fill: black;");
            } catch (Exception ex) {
                altUnit = null;
                altSymbol.setStyle("-fx-text-fill: #F18989;");
            }
        }

//        System.out.println("1: " + baseUnit);
//        System.out.println("2: " + altUnit);
//        System.out.println("3: " + prefixUnit);
        //combine
        if (baseUnit != null) {
//            if (altUnit != null) {
//                if (prefixUnit != null && !prefixUnit.isEmpty()) {
//                    return UnitManager.getInstance().getUnitWithPrefix(altUnit, prefixUnit);
//                } else {
//                    return altUnit;
//                }
//            } else {
            if (prefixUnit != null && !prefixUnit.isEmpty()) {
                return UnitManager.getInstance().getUnitWithPrefix(baseUnit, prefixUnit);
            } else {
                return baseUnit;
            }
//            }
        } else {
            return Unit.ONE;
        }

    }

    public void printExample() {
        Unit finalUnit = getFinalUnit();
//        System.out.println("final: " + finalUnit);

//        example.setText((Measure.valueOf(12345.67, finalUnit)).toString());
        if (getAltSymbol() != null && !getAltSymbol().equals("")) {
            example.setText("1245.67 " + UnitManager.getInstance().formate(finalUnit.alternate(getAltSymbol())));
        } else {
            example.setText("1245.67 " + UnitManager.getInstance().formate(finalUnit));
        }

//        try {
//            System.out.println("p1: " + Unit.valueOf(UnitManager.getInstance().formate(finalUnit)));
//            System.out.println("p2: " + BaseUnit.valueOf(UnitManager.getInstance().formate(finalUnit)));
//        } catch (Exception ex) {
//
//        }
    }

    public UnitData getUnit() {
        return new UnitData(getFinalUnit(), altSymbol.getText());
    }

    public void searchUnit() {
        try {
            Unit searchUnit = Unit.valueOf(searchField.getText().trim());

            altSymbol.setText("");
            boxPrefix.getSelectionModel().selectFirst();
            boxQuantity.getSelectionModel().selectFirst();
            boxUnit.getSelectionModel().selectFirst();

            System.out.println("standart Unit: " + searchUnit.getStandardUnit());
            Iterator<Object> iterator = boxQuantity.getItems().iterator();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (obj instanceof Unit) {
                    Unit unit = (Unit) obj;
                    if (unit.getStandardUnit().equals(searchUnit)) {
                        System.out.println("found unit");
                        boxQuantity.getSelectionModel().select(unit);

                        Iterator<Object> iterator2 = boxUnit.getItems().iterator();
                        while (iterator2.hasNext()) {
                            Object obj2 = iterator2.next();
                            if (obj2 instanceof Unit) {
                                Unit unit2 = (Unit) obj2;
                                if (unit2.equals(searchUnit)) {
                                    boxUnit.getSelectionModel().select(unit2);
                                }
                            }
                        }
                    }
                }
            }
            searchField.setStyle("-fx-text-fill: black;");

        } catch (Exception ex) {
            System.out.println("Unkown Unit");
            ex.printStackTrace();
            searchField.setStyle("-fx-text-fill: red;");

        }

    }

}
