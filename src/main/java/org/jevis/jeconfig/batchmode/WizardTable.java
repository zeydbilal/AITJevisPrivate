package org.jevis.jeconfig.batchmode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TablePosition;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
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
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisUnit;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.tool.ImageConverter;

/**
 *
 * @author Bilal
 */
public class WizardTable {

    private final ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
    private ObservableList<SpreadsheetCell> cells;
    private ObservableList<String> listAttribute = FXCollections.observableArrayList();
    private SpreadsheetView spv;
    private GridBase grid;
    private Stage stage = new Stage();
    private JEVisClass createDataClass;
    private JEVisClass createDataPoint;
    private LinkedList<String> listObjectNames = new LinkedList<>();
    private int rowCount;
    private int columnCount;
    private ObservableList<String> columnHeaderNames = FXCollections.observableArrayList();
    private ObservableList<String> columnHeaderNamesDataTable = FXCollections.observableArrayList();
    private ObservableList<Pair<String, ArrayList<String>>> pairList = FXCollections.observableArrayList();
    private ObservableList<String> listUnits = FXCollections.observableArrayList();
    private ObservableList<String> listUnitSymbols = FXCollections.observableArrayList();

    public static enum Type {

        NEW, RENAME, EDIT
    };

    public static enum Response {

        NO, YES, CANCEL
    };

    private Response response = Response.CANCEL;

    public WizardTable() {
    }

    public Response show(Stage owner, final JEVisClass jclass, final JEVisObject parent, boolean fixClass, Type type, String objName) {
        ObservableList<JEVisClass> options = FXCollections.observableArrayList();
        try {
            if (type == Type.NEW) {
                options = FXCollections.observableArrayList(parent.getAllowedChildrenClasses());
            }
        } catch (JEVisException ex) {
            Logger.getLogger(WizardTable.class.getName()).log(Level.SEVERE, null, ex);
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

        int createDataClassIndex = 0;
        int createDataPointIndex = 0;

        for (int i = 0; i < options.size(); i++) {
            try {
                if (options.get(i).getName().equals("Data")) {
                    createDataClassIndex = i;
                }
                if (options.get(i).getName().equals("Data Point")) {
                    createDataPointIndex = i;
                }
            } catch (JEVisException ex) {
                Logger.getLogger(WizardTable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        createDataClass = options.get(createDataClassIndex);
        createDataPoint = options.get(createDataPointIndex);

        System.out.println("createDataClass :  " + createDataClass);

        Button createBtn = new Button("Create Structure");
        Button cancelBtn = new Button("Cancel");

        new CreateNewWizardTable(createBtn);

        BorderPane root = new BorderPane();
//        root.setPadding(new Insets(3));

        HBox hBoxTop = new HBox();
        hBoxTop.setSpacing(10);
//        hBoxTop.setPadding(new Insets(3, 3, 3, 3));
        Label lClass = new Label("Class:");
        Button help = new Button("Help", JEConfig.getImage("quick_help_icon.png", 22, 22));
        Separator sep1 = new Separator();
        hBoxTop.getChildren().addAll(lClass, sep1, help);

        root.setTop(hBoxTop);

        HBox hBoxBottom = new HBox();
        hBoxBottom.setSpacing(10);
//        hBoxBottom.setPadding(new Insets(0, 3, 3, 3));
        hBoxBottom.getChildren().addAll(createBtn, cancelBtn);
        hBoxBottom.setAlignment(Pos.BASELINE_RIGHT);
        root.setBottom(hBoxBottom);

        root.setCenter(spv);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("styles/Table.css");
        //FIXME KeyCombination.SHORTCUT_DOWN
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
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN), new Runnable() {

            @Override
            public void run() {
                try {

                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();

                    ObservableList<TablePosition> focusedCell = spv.getSelectionModel().getSelectedCells();

                    int currentRow = 0;
                    int currentColumn = 0;
                    String contentText = "";

                    int oldRow = currentRow;
                    int oldColumn = currentColumn;

                    for (final TablePosition<?, ?> p : focusedCell) {
                        currentRow = p.getRow();
                        currentColumn = p.getColumn();

                        if (oldRow != currentRow) {
                            contentText += "\n";

                        }

                        if (oldColumn != currentColumn) {
                            contentText += "\t";
                        }

                        oldRow = currentRow;
                        oldColumn = currentColumn;

                        String spcText = rows.get(currentRow).get(currentColumn).getText();

                        contentText += spcText;

                    }

                    String[] splitText = contentText.split("\n");
                    String clipText = "";

                    for (int i = 0; i < splitText.length; i++) {
                        clipText += splitText[i].trim();
                        clipText += "\n";
                    }

                    content.putString(clipText);
                    clipboard.setContent(content);

                } catch (NullPointerException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        createBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                stage.close();
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

        help.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WebBrowser webBrowser = new WebBrowser();
            }
        });

        stage.setTitle("Bulk Create");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setScene(scene);
        stage.setWidth(700);
        stage.setHeight(1000);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(true);
        stage.showAndWait();

        return response;
    }

    public LinkedList<String> getlistObjectNames() {
        return listObjectNames;
    }

    public ObservableList<Pair<String, ArrayList<String>>> getPairList() {
        return pairList;
    }

    public JEVisClass createDataClass() {
        return createDataClass;
    }

    public ObservableList<String> getColumnHeaderNames() {
        return columnHeaderNames;
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

        public CreateNewWizardTable(Button createBtn) {
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

            ObservableList<SpreadsheetColumn> colList = spv.getColumns();

            for (SpreadsheetColumn colListElement : colList) {
                colListElement.setPrefWidth(150);
            }

            spv.setEditable(true);
            spv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            columnHeaderNamesDataTable.addAll(colNames);

            spv.getGrid().getColumnHeaders().addAll(columnHeaderNamesDataTable);
            //change it
            //createBtn.setDisable(true);
            addUnits();
            addSymbols();
            //GridChange Event for Prefix and Symbol Input Control
            spv.getGrid().addEventHandler(GridChange.GRID_CHANGE_EVENT, new EventHandler<GridChange>() {

                @Override
                public void handle(GridChange event) {
                    inputControl(createBtn);
                }
            });
        }
    }

    public void inputControl(Button createBtn) {
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
            createBtn.setDisable(false);
        } else {
            createBtn.setDisable(true);
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
