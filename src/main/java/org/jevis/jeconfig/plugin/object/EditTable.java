/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TablePosition;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Pair;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.GridChange;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisSample;
import org.jevis.api.JEVisUnit;
import org.jevis.jeconfig.tool.ImageConverter;

/**
 *
 * @author CalisZ
 */
public class EditTable {

    private final ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
    private ObservableList<SpreadsheetCell> cells;
    private SpreadsheetView spv;
    private GridBase grid;
    private Stage stage = new Stage();
    private JEVisClass selectedClass;
    private int rowCount;
    private int columnCount;
    private ObservableList<String> columnHeaderNames = FXCollections.observableArrayList();
    private ObservableList<String> columnHeaderNamesDataTable = FXCollections.observableArrayList();
    private ObservableList<Pair<String, ArrayList<String>>> pairList = FXCollections.observableArrayList();
    private ObservableList<String> listUnits = FXCollections.observableArrayList();
    private ObservableList<String> listUnitSymbols = FXCollections.observableArrayList();

    private Map<Integer, Double> generateRowHeight() {
        Map<Integer, Double> rowHeight = new HashMap<>();
        for (int i = 0; i < grid.getRowCount(); i++) {
            rowHeight.put(i, 30.0);
        }
        return rowHeight;
    }

    class CreateNewEditTable {

        private ObservableList<Pair<String, ObservableList<Pair<String, String>>>> listObjectAndSample = FXCollections.observableArrayList();

        public CreateNewEditTable(JEVisObject parent) {
            try {
                rowCount = parent.getChildren().size();
                columnCount = selectedClass.getTypes().size() + 1;
            } catch (JEVisException ex) {
                Logger.getLogger(CreateNewEditTable.class.getName()).log(Level.SEVERE, null, ex);
            }

            grid = new GridBase(rowCount, columnCount);

            for (int row = 0; row < grid.getRowCount(); ++row) {
                cells = FXCollections.observableArrayList();
                for (int column = 0; column < grid.getColumnCount(); ++column) {
                    cells.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1, ""));
                }

                rows.add(cells);
            }
            grid.setRows(rows);
            grid.setRowHeightCallback(new GridBase.MapBasedRowHeightFactory(generateRowHeight()));
            spv = new SpreadsheetView();
            spv.setGrid(grid);

            ObservableList<SpreadsheetColumn> colList = spv.getColumns();

            for (SpreadsheetColumn colListElement : colList) {
                colListElement.setPrefWidth(150);
            }

            spv.setEditable(true);
            spv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            columnHeaderNames.add("Object Name");
            try {
                //Get and set Typenames
                for (int i = 0; i < selectedClass.getTypes().size(); i++) {
                    columnHeaderNames.add(selectedClass.getTypes().get(i).getName());
                }

            } catch (JEVisException ex) {
                Logger.getLogger(CreateNewEditTable.class.getName()).log(Level.SEVERE, null, ex);
            }
            spv.getGrid().getColumnHeaders().addAll(columnHeaderNames);

            //Add to listObjectAndSample
            try {
                for (int i = 0; i < grid.getRowCount(); i++) {
                    //Get object name
                    String spcObjectName = parent.getChildren().get(i).getName();
                    // Get attributes
                    List<JEVisAttribute> attributes = parent.getChildren().get(i).getAttributes();

                    ObservableList<Pair<String, String>> listSample = FXCollections.observableArrayList();

                    for (int z = 0; z < attributes.size(); z++) {
                        if (attributes.get(z).getLatestSample() != null) {
                            //Get the last sample for this attribute
                            JEVisSample lastSample = attributes.get(z).getLatestSample();
                            // Add the last attribute name und value in the list.
                            listSample.add(new Pair(lastSample.getAttribute().getName(), lastSample.getValueAsString()));
                        } else {
                            listSample.add(new Pair(attributes.get(z).getName(), ""));
                        }
                    }
                    listObjectAndSample.add(new Pair(spcObjectName, listSample));
                }
            } catch (JEVisException ex) {
                Logger.getLogger(EditTable.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Add to table
            for (int i = 0; i < grid.getRowCount(); i++) {
                for (int j = 0; j < grid.getColumnCount(); j++) {
                    if (columnHeaderNames.get(j).equals("Object Name")) {
                        grid.setCellValue(i, columnHeaderNames.get(j).indexOf("Object Name"), listObjectAndSample.get(i).getKey());
                    } else {
                        int counter = 1;
                        for (int k = 0; k < listObjectAndSample.get(i).getValue().size(); k++) {
//                            if (listObjectAndSample.get(i).getValue().get(k).getKey().equals("Password")) {
//                                //TODO Password sonderfall ?
//                                grid.setCellValue(i, counter, "*******");
//                            }

                            if (listObjectAndSample.get(i).getValue().get(k).getValue().equals("true")) {
                                grid.setCellValue(i, counter, "1");
                            } else if (listObjectAndSample.get(i).getValue().get(k).getValue().equals("false")) {
                                grid.setCellValue(i, counter, "0");
                            } else {
                                grid.setCellValue(i, counter, listObjectAndSample.get(i).getValue().get(k).getValue());
                            }
                            counter++;
                        }
                    }
                }
            }
        }
    }

    public static enum Type {

        NEW, RENAME
    };

    public static enum Response {

        NO, YES, CANCEL
    };

    private Response response = Response.CANCEL;

    public EditTable() {

    }

    public Response show(Stage owner, final JEVisClass jclass, final JEVisObject parent, boolean fixClass, Type type, String objName) {
        ObservableList<JEVisClass> options = FXCollections.observableArrayList();
        try {
            if (type == Type.NEW) {
                options = FXCollections.observableArrayList(parent.getAllowedChildrenClasses());
            }
        } catch (JEVisException ex) {
            Logger.getLogger(CreateTable.class.getName()).log(Level.SEVERE, null, ex);
        }

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

        ComboBox<JEVisClass> classComboBox = new ComboBox<JEVisClass>(options);
        classComboBox.setCellFactory(cellFactory);
        classComboBox.setButtonCell(cellFactory.call(null));
        classComboBox.setMinWidth(250);
        classComboBox.getSelectionModel().selectFirst();
        selectedClass = classComboBox.getSelectionModel().getSelectedItem();

        Button editBtn = new Button("Edit Structure");
        Button cancelBtn = new Button("Cancel");

        try {
            if (selectedClass.getName().equals("Data")) {
                new CreateNewDataEditTable(parent, editBtn);
            } else {
                new CreateNewEditTable(parent);
            }
        } catch (JEVisException ex) {
            Logger.getLogger(CreateTable.class.getName()).log(Level.SEVERE, null, ex);
        }

        BorderPane root = new BorderPane();

        HBox hBoxTop = new HBox();
        hBoxTop.getChildren().addAll(classComboBox);
        root.setTop(hBoxTop);

        HBox hBoxBottom = new HBox();
        hBoxBottom.getChildren().addAll(editBtn, cancelBtn);
        hBoxBottom.setAlignment(Pos.BASELINE_RIGHT);
        root.setBottom(hBoxBottom);

        root.setCenter(spv);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("styles/Table.css");

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY), new Runnable() {

            @Override
            public void run() {
                try {
                    Clipboard clipboard = Clipboard.getSystemClipboard();

                    if (clipboard.hasString()) {

                        String[] words = clipboard.getString().split("\n");

                        ObservableList<TablePosition> focusedCell = spv.getSelectionModel().getSelectedCells();

                        int currentRow = 0;
                        int currentColumn = 0;

                        for (final TablePosition<?, ?> p : focusedCell) {
                            currentRow = p.getRow();
                            currentColumn = p.getColumn();
                        }

                        for (String word : words) {
                            String[] parseWord = word.split("\t");
                            int col = currentColumn;
                            for (int i = 0; i < parseWord.length; i++) {
                                SpreadsheetCell spc = rows.get(currentRow).get(col);
                                grid.setCellValue(currentRow, col, spc.getCellType().convertValue(parseWord[i].trim()));
                                col++;
                            }
                            currentRow++;
                        }

                    } else {
                        spv.pasteClipboard();
                    }
                } catch (NullPointerException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        editBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                stage.close();
                for (int i = 0; i < grid.getRowCount(); i++) {
                    SpreadsheetCell spcObjectName = rows.get(i).get(0);

                    if (!spcObjectName.getText().equals("")) {
                        ArrayList<String> attributes = new ArrayList<>();
                        for (int j = 1; j < grid.getColumnCount(); j++) {
                            SpreadsheetCell spcAttribut = rows.get(i).get(j);
                            attributes.add(spcAttribut.getText());
                        }
                        pairList.add(new Pair(spcObjectName.getText(), attributes));
                    }
                }
                response = Response.YES;
            }
        });

        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                stage.close();
                response = Response.CANCEL;

            }
        });

        classComboBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    rows.clear();
                    columnHeaderNames.clear();
                    columnHeaderNamesDataTable.clear();
                    selectedClass = classComboBox.getSelectionModel().getSelectedItem();

                    if (selectedClass.getName().equals("Data")) {
                        new CreateNewDataEditTable(parent, editBtn);
                        root.setCenter(spv);
                    } else {
                        new CreateNewEditTable(parent);
                        root.setCenter(spv);
                    }
                } catch (JEVisException ex) {
                    Logger.getLogger(CreateTable.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        stage.setTitle("Bulk Edit");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setScene(scene);
        stage.setWidth(1000);
        stage.setHeight(1000);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(true);
        stage.showAndWait();

        return response;
    }

    public ObservableList<Pair<String, ArrayList<String>>> getPairList() {
        return pairList;
    }

    public JEVisClass getSelectedClass() {
        return selectedClass;
    }

    private void addUnits() {
        JEVisUnit.Prefix[] prefixes = JEVisUnit.Prefix.values();

        for (int i = 0; i < prefixes.length; i++) {
            String strPrefix = prefixes[i].toString();
            listUnits.add(strPrefix);
        }
    }

    public void addSymbols() {
        // Indische Währungssymbol nicht in der Liste ("₦","..",)
        listUnitSymbols.addAll("m/s²",
                "g", "mol", "atom", "rad", "bit", "%", "centiradian", "dB", "°", "'", "byte", "rev", "¨", "sphere", "sr", "rad/s²", "rad/s", "Bq", "Ci", "Hz",
                "m²", "a", "ha", "cm²", "km²", "kat", "€", "₦", "$", "*?*", "¥", "Hits/cm²", "Hits/m²", "Ω/cm²", "bit/s", "-", "s", "m", "h", "day", "day_sidereal",
                "week", "month", "year", "year_calendar", "year_sidereal", "g/(cms)", "F", "C", "e", "Fd", "Fr", "S", "A", "Gi", "H", "V", "Ω", "J",
                "eV", "erg", "N", "dyn", "kgf", "lbf", "lx", "La", "W/m²", "m²/s", "cm²/s", "Å", "ua", "cm", "foot_survey_us", "ft", "in", "km", "ly",
                "mi", "mm", "nmi", "pc", "pixel", "pt", "yd", "W", "Wb", "Mx", "T", "G", "kg", "u", "me", "t", "oz", "lb", "ton_uk", "ton_us", "kg/s",
                "cd", "hp", "lm", "var", "Pa", "atm", "bar", "in Hg", "mmHg", "Gy", "rem", "Sv", "rd", "Rd", "rev/s", "grade", "K", "℃", "°F", "°R",
                "Nm", "Wh", "Ws", "m/s", "c", "km/h", "kn", "Mach", "mph", "m³", "in³", "gallon_dry_us", "gal", "gallon_uk", "l", "oz_uk", "kg/m³", "m³/s");
    }

    //TODO
    class CreateNewDataEditTable {

        private ObservableList<Pair<String, ObservableList<Pair<String, String>>>> listObjectAndValueAttribute = FXCollections.observableArrayList();

        public CreateNewDataEditTable(JEVisObject parent, Button editBtn) {

            String[] colNames = {"Object Name", "Display Prefix", "Display Symbol", "Display Sample Rate", "Input Prefix", "Input Symbol", "Input Sample Rate"};
            try {
                rowCount = parent.getChildren().size();
                columnCount = colNames.length;
            } catch (JEVisException ex) {
                Logger.getLogger(EditTable.class.getName()).log(Level.SEVERE, null, ex);
            }

            grid = new GridBase(rowCount, columnCount);

            for (int row = 0; row < grid.getRowCount(); ++row) {
                cells = FXCollections.observableArrayList();
                for (int column = 0; column < grid.getColumnCount(); ++column) {
                    cells.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1, ""));
                }
                rows.add(cells);
            }
            grid.setRows(rows);
            grid.setRowHeightCallback(new GridBase.MapBasedRowHeightFactory(generateRowHeight()));
            spv = new SpreadsheetView();
            spv.setGrid(grid);

            ObservableList<SpreadsheetColumn> colList = spv.getColumns();

            for (SpreadsheetColumn colListElement : colList) {
                colListElement.setPrefWidth(150);
            }

            spv.setEditable(true);
            spv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            columnHeaderNamesDataTable.addAll(colNames);

            spv.getGrid().getColumnHeaders().addAll(columnHeaderNamesDataTable);
            //change it
            //editBtn.setDisable(true);

            //TODO Add to list(listObjectAndAttribute)
            try {
                for (int i = 0; i < grid.getRowCount(); i++) {
                    //Get object name
                    String spcObjectName = parent.getChildren().get(i).getName();
                    // Get attributes
                    List<JEVisAttribute> attributes = parent.getChildren().get(i).getAttributes();
                    ObservableList<Pair<String, String>> listValueAttribute = FXCollections.observableArrayList();

                    for (int z = 0; z < attributes.size(); z++) {

                        JEVisUnit displayUnit = attributes.get(z).getDisplayUnit();
                        String displayPrefix = attributes.get(z).getDisplayUnit().getPrefix().toString();
                        String displaySampleRate = attributes.get(z).getDisplaySampleRate().toString();

                        JEVisUnit inputUnit = attributes.get(z).getInputUnit();
                        String inputSampleRate = attributes.get(z).getInputSampleRate().toString();
                        String inputPrefix = attributes.get(z).getInputUnit().getPrefix().toString();

                        if (attributes.get(z).getName().equals("Value")) {
                            if (displayPrefix.equals("") || displayPrefix.equals(null) || displayPrefix.equals("NONE")) {
                                listValueAttribute.add(new Pair(attributes.get(z).getName(), ""));
                            } else {
                                listValueAttribute.add(new Pair(attributes.get(z).getName(), displayPrefix));
                            }

                            if (displayUnit.toString().equals("") || displayUnit.equals(null) || displayUnit.toString().equals("NONE")) {
                                listValueAttribute.add(new Pair(attributes.get(z).getName(), ""));
                            } else {
                                if (displayUnit.toString().substring(0, 2).equals("da")) {
                                    listValueAttribute.add(new Pair(attributes.get(z).getName(), displayUnit.toString().substring(2, displayUnit.toString().length())));
                                } else {
                                    listValueAttribute.add(new Pair(attributes.get(z).getName(), displayUnit.toString().substring(1, displayUnit.toString().length())));
                                }
                            }

                            if (displaySampleRate.equals("") || displaySampleRate.equals(null) || displaySampleRate.equals("NONE")) {
                                listValueAttribute.add(new Pair(attributes.get(z).getName(), ""));
                            } else {
                                listValueAttribute.add(new Pair(attributes.get(z).getName(), displaySampleRate));
                            }

                            if (inputPrefix.equals("") || inputPrefix.equals(null) || inputPrefix.equals("NONE")) {
                                listValueAttribute.add(new Pair(attributes.get(z).getName(), ""));
                            } else {
                                listValueAttribute.add(new Pair(attributes.get(z).getName(), inputPrefix));
                            }

                            if (inputUnit.toString().equals("") || inputUnit.equals(null) || inputUnit.toString().equals("NONE")) {
                                listValueAttribute.add(new Pair(attributes.get(z).getName(), ""));
                            } else {
                                if (inputUnit.toString().substring(0, 2).equals("da")) {
                                    listValueAttribute.add(new Pair(attributes.get(z).getName(), inputUnit.toString().substring(2, inputUnit.toString().length())));
                                } else {
                                    listValueAttribute.add(new Pair(attributes.get(z).getName(), inputUnit.toString().substring(1, inputUnit.toString().length())));

                                }
                            }
                            listValueAttribute.add(new Pair(attributes.get(z).getName(), inputSampleRate));
                        }
                    }

                    listObjectAndValueAttribute.add(new Pair(spcObjectName, listValueAttribute));
                }
            } catch (JEVisException ex) {
                Logger.getLogger(EditTable.class.getName()).log(Level.SEVERE, null, ex);
            }

            //TODO Add to table
            for (int i = 0; i < grid.getRowCount(); i++) {
                for (int j = 0; j < grid.getColumnCount(); j++) {
                    if (columnHeaderNamesDataTable.get(j).equals("Object Name")) {
                        grid.setCellValue(i, columnHeaderNamesDataTable.get(j).indexOf("Object Name"), listObjectAndValueAttribute.get(i).getKey());
                    } else {
                        int counter = 1;
                        for (int k = 0; k < listObjectAndValueAttribute.get(i).getValue().size(); k++) {
                            grid.setCellValue(i, counter, listObjectAndValueAttribute.get(i).getValue().get(k).getValue());
                            counter++;
                        }
                    }
                }
            }

            addUnits();
            addSymbols();
            //GridChange Event for Prefix and Symbol Input Control
            spv.getGrid().addEventHandler(GridChange.GRID_CHANGE_EVENT, new EventHandler<GridChange>() {

                @Override
                public void handle(GridChange event) {
                    inputControl(editBtn);
                }
            });
        }
    }

    public void inputControl(Button editBtn) {
        ObservableList<String> listPrefix = FXCollections.observableArrayList();
        ObservableList<String> listSymbols = FXCollections.observableArrayList();
        ObservableList<String> listSampleRateControl = FXCollections.observableArrayList();

        Pattern pattern = Pattern.compile("[P]([0-9]+[M])?([0-9][W])?[T]([0-9]+[H])?([0-9]+[M])?([0-9]+[S])?");

        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcDisplayPrefix = rows.get(i).get(1);
            if (!spcDisplayPrefix.getText().equals("")) {
                listPrefix.add(spcDisplayPrefix.getText());
            }
        }
        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcInputPrefix = rows.get(i).get(4);
            if (!spcInputPrefix.getText().equals("")) {
                listPrefix.add(spcInputPrefix.getText());
            }
        }
        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcDisplaySymbol = rows.get(i).get(2);
            if (!spcDisplaySymbol.getText().equals("")) {
                listSymbols.add(spcDisplaySymbol.getText());
            }
        }
        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcInputSymbol = rows.get(i).get(5);
            if (!spcInputSymbol.getText().equals("")) {
                listSymbols.add(spcInputSymbol.getText());
            }
        }

        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcDisplaySampleRate = rows.get(i).get(3);
            if (!spcDisplaySampleRate.getText().equals("")) {
                Matcher matcher = pattern.matcher(spcDisplaySampleRate.getText());
                if (!matcher.matches()) {
                    listSampleRateControl.add(spcDisplaySampleRate.getText());
                }
            }
        }
        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcInputSampleRate = rows.get(i).get(6);
            if (!spcInputSampleRate.getText().equals("")) {
                Matcher matcher = pattern.matcher(spcInputSampleRate.getText());
                if (!matcher.matches()) {
                    listSampleRateControl.add(spcInputSampleRate.getText());
                }
            }
        }

        if (listUnits.containsAll(listPrefix) && listUnitSymbols.containsAll(listSymbols) && listSampleRateControl.isEmpty()) {
            editBtn.setDisable(false);
        } else {
            editBtn.setDisable(true);
        }

        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcDisplayPrefix = rows.get(i).get(1);
            if (!spcDisplayPrefix.getText().equals("")) {
                if (!listUnits.contains(spcDisplayPrefix.getText())) {
                    spcDisplayPrefix.getStyleClass().add("spreadsheet-cell-error");
                } else {
                    spcDisplayPrefix.getStyleClass().remove("spreadsheet-cell-error");
                }
            } else {
                spcDisplayPrefix.getStyleClass().remove("spreadsheet-cell-error");
            }
        }

        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcInputPrefix = rows.get(i).get(4);
            if (!spcInputPrefix.getText().equals("")) {
                if (!listUnits.contains(spcInputPrefix.getText())) {
                    spcInputPrefix.getStyleClass().add("spreadsheet-cell-error");
                } else {
                    spcInputPrefix.getStyleClass().remove("spreadsheet-cell-error");
                }
            } else {
                spcInputPrefix.getStyleClass().remove("spreadsheet-cell-error");
            }
        }
        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcDisplaySymbol = rows.get(i).get(2);
            if (!spcDisplaySymbol.getText().equals("")) {
                if (!listUnitSymbols.contains(spcDisplaySymbol.getText())) {
                    spcDisplaySymbol.getStyleClass().add("spreadsheet-cell-error");
                } else {
                    spcDisplaySymbol.getStyleClass().remove("spreadsheet-cell-error");
                }
            } else {
                spcDisplaySymbol.getStyleClass().remove("spreadsheet-cell-error");
            }
        }

        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcInputSymbol = rows.get(i).get(5);
            if (!spcInputSymbol.getText().equals("")) {
                if (!listUnitSymbols.contains(spcInputSymbol.getText())) {
                    spcInputSymbol.getStyleClass().add("spreadsheet-cell-error");
                } else {
                    spcInputSymbol.getStyleClass().remove("spreadsheet-cell-error");
                }
            } else {
                spcInputSymbol.getStyleClass().remove("spreadsheet-cell-error");
            }
        }

        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcDisplaySampleRate = rows.get(i).get(3);
            Matcher matcher = pattern.matcher(spcDisplaySampleRate.getText());
            if (!spcDisplaySampleRate.getText().equals("")) {
                if (!matcher.matches()) {
                    spcDisplaySampleRate.getStyleClass().add("spreadsheet-cell-error");
                } else {
                    spcDisplaySampleRate.getStyleClass().remove("spreadsheet-cell-error");
                }
            } else {
                spcDisplaySampleRate.getStyleClass().remove("spreadsheet-cell-error");
            }
        }

        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcInputSampleRate = rows.get(i).get(6);
            Matcher matcher = pattern.matcher(spcInputSampleRate.getText());
            if (!spcInputSampleRate.getText().equals("")) {
                if (!matcher.matches()) {
                    spcInputSampleRate.getStyleClass().add("spreadsheet-cell-error");
                } else {
                    spcInputSampleRate.getStyleClass().remove("spreadsheet-cell-error");
                }
            } else {
                spcInputSampleRate.getStyleClass().remove("spreadsheet-cell-error");
            }
        }

        listSymbols.clear();
        listPrefix.clear();
        listSampleRateControl.clear();
    }
}
