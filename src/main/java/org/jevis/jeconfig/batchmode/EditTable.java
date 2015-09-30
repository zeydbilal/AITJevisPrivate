/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
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
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.tool.ImageConverter;

/**
 *
 * @author Zeyd Bilal Calis
 */
public class EditTable {

    //declarations
    private Response response = Response.CANCEL;
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
    private ObservableList<JEVisObject> listChildren = FXCollections.observableArrayList();

    public EditTable() {

    }

    public static enum Type {

        NEW, RENAME, EDIT
    };

    public static enum Response {

        NO, YES, CANCEL
    };

    private void addListChildren(JEVisObject parent, JEVisClass selectedClass) {
        try {
            for (int i = 0; i < parent.getChildren(selectedClass, true).size(); i++) {
                listChildren.add(parent.getChildren(selectedClass, true).get(i));
            }
        } catch (JEVisException ex) {
            Logger.getLogger(EditTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ObservableList<JEVisObject> getListChildren() {
        return listChildren;
    }

    public Response show(Stage owner, final JEVisClass jclass, final JEVisObject parent, boolean fixClass, Type type, String objName) {
        ObservableList<JEVisClass> options = FXCollections.observableArrayList();
        try {
            if (type == Type.EDIT) {
                options = FXCollections.observableArrayList(parent.getAllowedChildrenClasses());
            }
        } catch (JEVisException ex) {
            Logger.getLogger(EditTable.class.getName()).log(Level.SEVERE, null, ex);
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
                                Logger.getLogger(EditTable.class.getName()).log(Level.SEVERE, null, ex);
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

        addListChildren(parent, selectedClass);
        Button editBtn = new Button("Edit Structure");
        Button cancelBtn = new Button("Cancel");

        try {
            if (selectedClass.getName().equals("Data")) {
                new CreateNewDataEditTable(parent, editBtn);
            } else {
                new CreateNewEditTable(parent);
            }
        } catch (JEVisException ex) {
            Logger.getLogger(EditTable.class.getName()).log(Level.SEVERE, null, ex);
        }

        BorderPane root = new BorderPane();
        //root.setPadding(new Insets(3));

        HBox hBoxTop = new HBox();
        hBoxTop.setSpacing(10);
        //hBoxTop.setPadding(new Insets(3, 3, 3, 3));
        Label lClass = new Label("Class:");
        Button help = new Button("Help", JEConfig.getImage("quick_help_icon.png", 22, 22));
        Separator sep1 = new Separator();
        hBoxTop.getChildren().addAll(lClass, classComboBox, sep1, help);
        root.setTop(hBoxTop);

        HBox hBoxBottom = new HBox();
        hBoxBottom.setSpacing(10);
        //hBoxBottom.setPadding(new Insets(3, 3, 3, 3));
        hBoxBottom.getChildren().addAll(editBtn, cancelBtn);
        hBoxBottom.setAlignment(Pos.BASELINE_RIGHT);
        root.setBottom(hBoxBottom);

        root.setCenter(spv);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("styles/Table.css");

        editBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                stage.close();
                for (int i = 0; i < grid.getRowCount(); i++) {
                    try {

                        String spcObjectName = rows.get(i).get(1).getText();

                        ArrayList<String> attributes = new ArrayList<>();
                        for (int j = 2; j < grid.getColumnCount(); j++) {
                            SpreadsheetCell spcAttribut = rows.get(i).get(j);
                            attributes.add(spcAttribut.getText());
                        }

                        pairList.add(new Pair(spcObjectName, attributes));

                        // set the new name from table
                        if (!listChildren.get(i).getName().equals(spcObjectName) && !spcObjectName.equals("")) {
                            listChildren.get(i).setName(spcObjectName);
                            listChildren.get(i).commit();
                        }

                    } catch (JEVisException ex) {
                        Logger.getLogger(EditTable.class.getName()).log(Level.SEVERE, null, ex);
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
                    pairList.clear();
                    listChildren.clear();
                    selectedClass = classComboBox.getSelectionModel().getSelectedItem();
                    addListChildren(parent, selectedClass);
                    if (selectedClass.getName().equals("Data")) {
                        new CreateNewDataEditTable(parent, editBtn);
                        root.setCenter(spv);
                    } else {
                        new CreateNewEditTable(parent);
                        root.setCenter(spv);
                    }
                } catch (JEVisException ex) {
                    Logger.getLogger(EditTable.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        help.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WebBrowser webBrowser = new WebBrowser();
            }
        });

        stage.setTitle("Bulk Edit");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setScene(scene);
        stage.setWidth(1250);
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

    // Erstelle eine neue Tabelle fuer die Objekte zu editieren.
    class CreateNewEditTable {

        private ObservableList<Pair<JEVisObject, ObservableList<Pair<String, String>>>> listObjectAndSample = FXCollections.observableArrayList();

        public CreateNewEditTable(JEVisObject parent) {
            try {
                rowCount = getListChildren().size();
                //Spalten-Anzahl : Klassen Attribute(Types) und +2 ist fuer die Object ID,Objectname.
                columnCount = selectedClass.getTypes().size() + 2;
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
            spv = new SpreadsheetViewTable(rows, grid);

            spv.setGrid(grid);

            ObservableList<SpreadsheetColumn> colList = spv.getColumns();

            for (SpreadsheetColumn colListElement : colList) {
                colListElement.setPrefWidth(150);
            }

            spv.setEditable(true);
            spv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            columnHeaderNames.add("Object ID");
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
                    // Get attributes
                    List<JEVisAttribute> attributes = listChildren.get(i).getAttributes();

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
                    listObjectAndSample.add(new Pair(listChildren.get(i), listSample));
                }
            } catch (JEVisException ex) {
                Logger.getLogger(EditTable.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Add to table
            //sortiere die Liste! Die Reihenfolge genau wie Baumsreihenfolge
            sortTheChildren(listChildren);
            sortTheAttribute(listObjectAndSample);

            for (int i = 0; i < grid.getRowCount(); i++) {
                for (int j = 0; j < grid.getColumnCount(); j++) {
                    if (columnHeaderNames.get(j).equals("Object ID")) {
                        //Object ID is not editable
                        grid.setCellValue(i, 0, listChildren.get(i).getID());
                        SpreadsheetCell cellIndex = rows.get(i).get(0);
                        cellIndex.getStyleClass().add("spreadsheet-cell-objectId");
                        cellIndex.setEditable(false);
                    } else if (columnHeaderNames.get(j).equals("Object Name")) {
                        grid.setCellValue(i, 1, listChildren.get(i).getName());
                    } else {
                        int counter = 2;
                        for (int k = 0; k < listObjectAndSample.get(i).getValue().size(); k++) {
                            if (listObjectAndSample.get(i).getValue().get(k).getKey().equals("Password")) {
                                //Password cell is not editable
                                grid.setCellValue(i, counter, listObjectAndSample.get(i).getValue().get(k).getValue());
                                SpreadsheetCell cellIndex = rows.get(i).get(counter);
                                cellIndex.setEditable(false);
                            }

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

    // Erstelle eine neue Tabelle fuer die Data-Objekte zu editieren.
    // Diese Klasse spezial nur fuer das Data-Object implementiert.
    class CreateNewDataEditTable {

        private ObservableList<Pair<JEVisObject, ObservableList<Pair<String, String>>>> listObjectAndValueAttribute = FXCollections.observableArrayList();

        public CreateNewDataEditTable(JEVisObject parent, Button editBtn) {

            String[] colNames = {"Object ID", "Object Name", "Display Prefix", "Display Symbol", "Display Sample Rate", "Input Prefix", "Input Symbol", "Input Sample Rate"};

            rowCount = getListChildren().size();
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
            grid.setRowHeightCallback(new GridBase.MapBasedRowHeightFactory(generateRowHeight()));
            spv = new SpreadsheetViewTable(rows, grid);

            spv.setGrid(grid);

            ObservableList<SpreadsheetColumn> colList = spv.getColumns();

            for (SpreadsheetColumn colListElement : colList) {
                colListElement.setPrefWidth(150);
            }

            spv.setEditable(true);
            spv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            columnHeaderNamesDataTable.addAll(colNames);

            spv.getGrid().getColumnHeaders().addAll(columnHeaderNamesDataTable);

            try {
                for (int i = 0; i < grid.getRowCount(); i++) {
                    // Get attributes
                    List<JEVisAttribute> attributes = listChildren.get(i).getAttributes();
                    ObservableList<Pair<String, String>> listValueAttribute = FXCollections.observableArrayList();

                    for (int z = 0; z < attributes.size(); z++) {

                        JEVisUnit displayUnit = attributes.get(z).getDisplayUnit();
                        String[] splitDisplayUnit = attributes.get(z).getDisplayUnit().toJSON().split("\"");

                        String displayPrefix = attributes.get(z).getDisplayUnit().getPrefix().toString();
                        String displaySampleRate = attributes.get(z).getDisplaySampleRate().toString();

                        JEVisUnit inputUnit = attributes.get(z).getInputUnit();
                        String[] splitInputUnit = attributes.get(z).getInputUnit().toJSON().split("\"");

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
                                listValueAttribute.add(new Pair(attributes.get(z).getName(), splitDisplayUnit[3]));

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
                                listValueAttribute.add(new Pair(attributes.get(z).getName(), splitInputUnit[3]));
                            }
                            listValueAttribute.add(new Pair(attributes.get(z).getName(), inputSampleRate));
                        }
                    }
                    listObjectAndValueAttribute.add(new Pair(listChildren.get(i), listValueAttribute));
                }
            } catch (JEVisException ex) {
                Logger.getLogger(EditTable.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Add to table
            //sortiere die Liste! Die Reihenfolge genau wie Baumsreihenfolge
            sortTheChildren(listChildren);
            sortTheAttribute(listObjectAndValueAttribute);
            for (int i = 0; i < grid.getRowCount(); i++) {
                for (int j = 0; j < grid.getColumnCount(); j++) {
                    if (columnHeaderNamesDataTable.get(j).equals("Object ID")) {
                        grid.setCellValue(i, 0, listChildren.get(i).getID());
                        SpreadsheetCell cellIndex = rows.get(i).get(0);
                        cellIndex.getStyleClass().add("spreadsheet-cell-objectId");
                        cellIndex.setEditable(false);
                    } else if (columnHeaderNamesDataTable.get(j).equals("Object Name")) {
                        grid.setCellValue(i, 1, listChildren.get(i).getName());
                    } else {
                        //Attribute ab zweite spalte einsetzen!
                        int counter = 2;
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

    public void inputControl(Button createBtn) {
        ObservableList<String> listPrefix = FXCollections.observableArrayList();
        ObservableList<String> listSymbols = FXCollections.observableArrayList();
        ObservableList<String> listSampleRateControl = FXCollections.observableArrayList();

        Pattern pattern = Pattern.compile("[P]([0-9]+[M])?([0-9][W])?[T]([0-9]+[H])?([0-9]+[M])?([0-9]+[S])?");

        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcDisplayPrefix = rows.get(i).get(2);
            if (!spcDisplayPrefix.getText().equals("")) {
                listPrefix.add(spcDisplayPrefix.getText());
            }
        }
        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcInputPrefix = rows.get(i).get(5);
            if (!spcInputPrefix.getText().equals("")) {
                listPrefix.add(spcInputPrefix.getText());
            }
        }
        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcDisplaySymbol = rows.get(i).get(3);
            if (!spcDisplaySymbol.getText().equals("")) {
                listSymbols.add(spcDisplaySymbol.getText());
            }
        }
        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcInputSymbol = rows.get(i).get(6);
            if (!spcInputSymbol.getText().equals("")) {
                listSymbols.add(spcInputSymbol.getText());
            }
        }

        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcDisplaySampleRate = rows.get(i).get(4);
            if (!spcDisplaySampleRate.getText().equals("")) {
                Matcher matcher = pattern.matcher(spcDisplaySampleRate.getText());
                if (!matcher.matches()) {
                    listSampleRateControl.add(spcDisplaySampleRate.getText());
                }
            }
        }
        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcInputSampleRate = rows.get(i).get(7);
            if (!spcInputSampleRate.getText().equals("")) {
                Matcher matcher = pattern.matcher(spcInputSampleRate.getText());
                if (!matcher.matches()) {
                    listSampleRateControl.add(spcInputSampleRate.getText());
                }
            }
        }

        if (listUnits.containsAll(listPrefix) && listUnitSymbols.containsAll(listSymbols) && listSampleRateControl.isEmpty()) {
            createBtn.setDisable(false);
        } else {
            createBtn.setDisable(true);
        }

        for (int i = 0; i < grid.getRowCount(); i++) {
            SpreadsheetCell spcDisplayPrefix = rows.get(i).get(2);
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
            SpreadsheetCell spcInputPrefix = rows.get(i).get(5);
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
            SpreadsheetCell spcDisplaySymbol = rows.get(i).get(3);
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
            SpreadsheetCell spcInputSymbol = rows.get(i).get(6);
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
            SpreadsheetCell spcDisplaySampleRate = rows.get(i).get(4);
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
            SpreadsheetCell spcInputSampleRate = rows.get(i).get(7);
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

    // Hier wird die Zellenhöhe anpasst
    private Map<Integer, Double> generateRowHeight() {
        Map<Integer, Double> rowHeight = new HashMap<>();
        for (int i = 0; i < grid.getRowCount(); i++) {
            rowHeight.put(i, 30.0);
        }
        return rowHeight;
    }

    public static void sortTheChildren(ObservableList<JEVisObject> list) {
        Comparator<JEVisObject> sort = new Comparator<JEVisObject>() {

            @Override
            public int compare(JEVisObject o1, JEVisObject o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        FXCollections.sort(list, sort);
    }

    public static void sortTheAttribute(ObservableList<Pair<JEVisObject, ObservableList<Pair<String, String>>>> list) {
        Comparator<Pair<JEVisObject, ObservableList<Pair<String, String>>>> sort = new Comparator<Pair<JEVisObject, ObservableList<Pair<String, String>>>>() {

            @Override
            public int compare(Pair<JEVisObject, ObservableList<Pair<String, String>>> o1, Pair<JEVisObject, ObservableList<Pair<String, String>>> o2) {
                return o1.getKey().getName().compareTo(o2.getKey().getName());
            }
        };

        FXCollections.sort(list, sort);
    }

    private void addUnits() {
        JEVisUnit.Prefix[] prefixes = JEVisUnit.Prefix.values();

        for (int i = 0; i < prefixes.length; i++) {
            String strPrefix = prefixes[i].toString();
            listUnits.add(strPrefix);
        }
    }

    public void addSymbols() {
        listUnitSymbols.addAll("m/s²",
                "g", "mol", "atom", "rad", "bit", "%", "centiradian", "dB", "°", "'", "byte", "rev", "¨", "sphere", "sr", "rad/s²", "rad/s", "Bq", "Ci", "Hz",
                "m²", "a", "ha", "cm²", "km²", "kat", "€", "₦", "\u20B9", "$", "*?*", "¥", "Hits/cm²", "Hits/m²", "Ω/cm²", "bit/s", "-", "s", "m", "h", "day", "day_sidereal",
                "week", "month", "year", "year_calendar", "year_sidereal", "g/(cms)", "F", "C", "e", "Fd", "Fr", "S", "A", "Gi", "H", "V", "Ω", "J",
                "eV", "erg", "N", "dyn", "kgf", "lbf", "lx", "La", "W/m²", "m²/s", "cm²/s", "Å", "ua", "cm", "foot_survey_us", "ft", "in", "km", "ly",
                "mi", "mm", "nmi", "pc", "pixel", "pt", "yd", "W", "Wb", "Mx", "T", "G", "kg", "u", "me", "t", "oz", "lb", "ton_uk", "ton_us", "kg/s",
                "cd", "hp", "lm", "var", "Pa", "atm", "bar", "in Hg", "mmHg", "Gy", "rem", "Sv", "rd", "Rd", "rev/s", "grade", "K", "℃", "°F", "°R",
                "Nm", "Wh", "Ws", "m/s", "c", "km/h", "kn", "Mach", "mph", "m³", "in³", "gallon_dry_us", "gal", "gallon_uk", "l", "oz_uk", "kg/m³", "m³/s");
    }
}
