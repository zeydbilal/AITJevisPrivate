/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import java.util.ArrayList;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.GridChange;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.jevis.api.JEVisUnit;
import org.jevis.jeconfig.plugin.object.ObjectTree;

/**
 *
 * @author CalisZ
 */
public class ManualWizardStep4 extends WizardPane {

    private final ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
    private ObservableList<SpreadsheetCell> cells;
    private GridBase grid;
    private SpreadsheetView spv;
    private WizardSelectedObject wizardSelectedObject;
    private ObjectTree tree;
    private Button finish;
    private int rowCount;
    private int columnCount;
    private ObservableList<String> columnHeaderNames = FXCollections.observableArrayList();
    private ObservableList<String> columnHeaderNamesDataTable = FXCollections.observableArrayList();
    private ObservableList<Pair<String, ArrayList<String>>> pairList = FXCollections.observableArrayList();
    private ObservableList<String> listUnits = FXCollections.observableArrayList();
    private ObservableList<String> listUnitSymbols = FXCollections.observableArrayList();

    public ManualWizardStep4(ObjectTree tree, WizardSelectedObject wizardSelectedObject) {
        this.wizardSelectedObject = wizardSelectedObject;
        this.tree = tree;
        setMinSize(700, 830);

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
//            if (type.getButtonData().equals(ButtonBar.ButtonData.FINISH)) {
//                Node finish = lookupButton(type);
//                finish.disableProperty().setValue(Boolean.TRUE);
//            }

            if (type.getButtonData().equals(ButtonBar.ButtonData.FINISH)) {
                finish = (Button) lookupButton(type);
                finish.setDisable(true);
                finish.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //TODO Exiting Step 4........!!!!!!!!!!

                        //TODO Speichere alles in die pairList ab.
                        for (int i = 0; i < grid.getRowCount(); i++) {
                            String spcObjectName = rows.get(i).get(0).getText();

                            if (!spcObjectName.equals("")) {
                                ArrayList<String> attributs = new ArrayList<>();
                                for (int j = 1; j < grid.getColumnCount(); j++) {
                                    SpreadsheetCell spcAttribut = rows.get(i).get(j);
                                    attributs.add(spcAttribut.getText());
                                }
                                pairList.add(new Pair(spcObjectName, attributs));
                            }
                        }

                        //TODO Erzeuge Data Object
                        
                        //TODO Ließ alles von der pairList ab und erzeuge die Objekte.
                        for (Pair<String, ArrayList<String>> pair : pairList) {
                            for (String value : pair.getValue()) {
                                System.out.println(pair.getKey() + value);
                            }
                        }

                        //Expand the building Object.
                        tree.expandSelected(true);
                    }
                });
            }
        }
        setContent(getInit());
    }
//Remove this !!
//    @Override
//    public void onExitingPage(Wizard wizard) {
//
//        ObservableList<ButtonType> list = getButtonTypes();
//        for (ButtonType type : list) {
//            if (type.getButtonData().equals(ButtonBar.ButtonData.FINISH)) {
//                System.out.println("SelectedObject Tree : " + tree.getSelectedObject().getName());
//                tree.expandSelected(true);
//            }
//        }
//    }

    private BorderPane getInit() {
        new CreateNewWizardTable(finish);

        BorderPane root = new BorderPane();

        root.setCenter(spv);
        return root;

    }

    private void addUnits() {
        JEVisUnit.Prefix[] prefixes = JEVisUnit.Prefix.values();

        for (int i = 0; i < prefixes.length; i++) {
            String strPrefix = prefixes[i].toString();
            listUnits.add(strPrefix);
        }
    }

    public void addSymbols() {
        listUnitSymbols.addAll("m/sÂ²",
                "g", "mol", "atom", "rad", "bit", "%", "centiradian", "dB", "Â°", "'", "byte", "rev", "Â¨", "sphere", "sr", "rad/sÂ²", "rad/s", "Bq", "Ci", "Hz",
                "mÂ²", "a", "ha", "cmÂ²", "kmÂ²", "kat", "â‚¬", "â‚¦", "\u20B9", "$", "*?*", "Â¥", "Hits/cmÂ²", "Hits/mÂ²", "Î©/cmÂ²", "bit/s", "-", "s", "m", "h", "day", "day_sidereal",
                "week", "month", "year", "year_calendar", "year_sidereal", "g/(cms)", "F", "C", "e", "Fd", "Fr", "S", "A", "Gi", "H", "V", "Î©", "J",
                "eV", "erg", "N", "dyn", "kgf", "lbf", "lx", "La", "W/mÂ²", "mÂ²/s", "cmÂ²/s", "Ã…", "ua", "cm", "foot_survey_us", "ft", "in", "km", "ly",
                "mi", "mm", "nmi", "pc", "pixel", "pt", "yd", "W", "Wb", "Mx", "T", "G", "kg", "u", "me", "t", "oz", "lb", "ton_uk", "ton_us", "kg/s",
                "cd", "hp", "lm", "var", "Pa", "atm", "bar", "in Hg", "mmHg", "Gy", "rem", "Sv", "rd", "Rd", "rev/s", "grade", "K", "â„ƒ", "Â°F", "Â°R",
                "Nm", "Wh", "Ws", "m/s", "c", "km/h", "kn", "Mach", "mph", "mÂ³", "inÂ³", "gallon_dry_us", "gal", "gallon_uk", "l", "oz_uk", "kg/mÂ³", "mÂ³/s");
    }

    class CreateNewWizardTable {

        public CreateNewWizardTable(Button finish) {
            String[] colNames = {"Object Name", "Prefix", "Symbol", "Channel/Target"};
            rowCount = 1000;
            columnCount = colNames.length;

            grid = new GridBase(rowCount, columnCount);

            for (int row = 0; row < grid.getRowCount(); ++row) {
                cells = FXCollections.observableArrayList();
                for (int column = 0; column < grid.getColumnCount(); ++column) {
                    cells.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1, ""));
                }
                rows.add(cells);
            }
            grid.setRows(rows);
            spv = new SpreadsheetView();
            spv.setGrid(grid);
            spv.getStylesheets().add("styles/Table.css");
            ObservableList<SpreadsheetColumn> colList = spv.getColumns();

            for (SpreadsheetColumn colListElement : colList) {
                colListElement.setPrefWidth(150);
            }

            spv.setEditable(true);
            spv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            columnHeaderNamesDataTable.addAll(colNames);

            spv.getGrid().getColumnHeaders().addAll(columnHeaderNamesDataTable);

            addUnits();
            addSymbols();
            //GridChange Event for Prefix and Symbol Input Control
            spv.getGrid().addEventHandler(GridChange.GRID_CHANGE_EVENT, new EventHandler<GridChange>() {

                @Override
                public void handle(GridChange event) {
                    inputControl(finish);
                }
            });
        }
    }

    public void inputControl(Button finish) {
        ObservableList<String> listPrefix = FXCollections.observableArrayList();
        ObservableList<String> listSymbols = FXCollections.observableArrayList();

        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcPrefix = rows.get(i).get(1);
            if (!spcPrefix.getText().equals("")) {
                listPrefix.add(spcPrefix.getText());
            }
        }
        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcSymbol = rows.get(i).get(2);
            if (!spcSymbol.getText().equals("")) {
                listSymbols.add(spcSymbol.getText());
            }
        }

        if (listUnits.containsAll(listPrefix) && listUnitSymbols.containsAll(listSymbols)) {
            finish.setDisable(false);
        } else {
            finish.setDisable(true);
        }

        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcPrefix = rows.get(i).get(1);
            if (!spcPrefix.getText().equals("")) {
                if (!listUnits.contains(spcPrefix.getText())) {
                    spcPrefix.getStyleClass().add("spreadsheet-cell-error");
                } else {
                    spcPrefix.getStyleClass().remove("spreadsheet-cell-error");
                }
            } else {
                spcPrefix.getStyleClass().remove("spreadsheet-cell-error");
            }
        }

        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcSymbol = rows.get(i).get(2);
            if (!spcSymbol.getText().equals("")) {
                if (!listUnitSymbols.contains(spcSymbol.getText())) {
                    spcSymbol.getStyleClass().add("spreadsheet-cell-error");
                } else {
                    spcSymbol.getStyleClass().remove("spreadsheet-cell-error");
                }
            } else {
                spcSymbol.getStyleClass().remove("spreadsheet-cell-error");
            }
        }

        listSymbols.clear();
        listPrefix.clear();
    }
}
