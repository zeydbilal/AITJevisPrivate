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
package org.jevis.jeconfig.sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisSample;
import org.jevis.application.dialog.ExceptionDialog;
import org.jevis.application.dialog.InfoDialog;
import org.jevis.jeconfig.JEConfig;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * This Dialog export JEVisSamples as csv files with different configurations.
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class SampleExportExtension implements SampleEditorExtension {

    public static String ICON = "1415654364_stock_export.png";

    private final static String TITEL = "Export";
    private final BorderPane _view = new BorderPane();
    private JEVisAttribute _att;

    public static enum Response {

        YES, CANCEL
    };

    public static enum FIELDS {

        DATE, TIME, VALUE, DATE_TIME
    };

    final Button ok = new Button("OK");
    Label lLineSep = new Label("Field Seperator:");
    TextField fLineSep = new TextField(";");
    Label lEnclosedBy = new Label("Enclosed by:");
    TextField fEnclosedBy = new TextField("");

    Label lDateTimeFormat = new Label("Date Formate:");
    TextField fDateTimeFormat = new TextField("yyyy-MM-dd HH:mm:ss");
    Label lTimeFormate = new Label("Time Formate:");
    Label lDateFormat = new Label("Date Formate:");
    TextField fTimeFormate = new TextField("HH:mm:ss");
    TextField fDateFormat = new TextField("yyyy-MM-dd");

    RadioButton bDateTime = new RadioButton("Date and time in one field:");
    RadioButton bDateTime2 = new RadioButton("Date and time seperated:");

    Label lValueFormate = new Label("Value Formate:");
    TextField fValueFormat = new TextField("###.###");

    Label lHeader = new Label("Custom CSV Header");
    TextField fHeader = new TextField("Example header mit Attribute namen");

    Label lExample = new Label("Preview:");
    TextArea fTextArea = new TextArea("Example");

    Label lPFilePath = new Label("File:");
    TextField fFile = new TextField();
    Button bFile = new Button("Change");

    Button export = new Button("Export");

    File destinationFile;

    List<JEVisSample> _samples = new ArrayList<>();

    JEVisAttribute attriute;

    TableView tabel = new TableView();
    TableColumn dateTimeColumn = new TableColumn("Datetime");
    TableColumn dateColum = new TableColumn("Date");
    TableColumn valueColum = new TableColumn("Value");
    TableColumn timeColum = new TableColumn("Time");
    private boolean _isBuild = false;

    public SampleExportExtension(JEVisAttribute att) {
        _att = att;

    }

    @Override
    public boolean isForAttribute(JEVisAttribute obj) {
        return true;
    }

    @Override
    public Node getView() {
        return _view;
    }

    @Override
    public String getTitel() {
        return TITEL;
    }

    @Override
    public void update() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (!_isBuild) {
                    buildGUI(_att, _samples);
                } else {
//                    _samples = samples;
//                    updateOderField();
                    updatePreview();
                }
            }
        });
    }

    @Override
    public void setSamples(final JEVisAttribute att, final List<JEVisSample> samples) {
        _samples = samples;
        _att = att;
    }

    public boolean doExport() throws FileNotFoundException, UnsupportedEncodingException {

        String exportStrg = createCSVString(Integer.MAX_VALUE);

        if (!fFile.getText().isEmpty() && exportStrg.length() > 90) {
            writeFile(fFile.getText(), exportStrg);
            return true;
        }

        return false;

    }

    @Override
    public boolean sendOKAction() {
        System.out.println("sendOk to Export");
        try {
            InfoDialog info = new InfoDialog();
            if (doExport()) {
                info.show(JEConfig.getStage(), "Success", "Export was successful", "Export was successful");
                return true;
            } else {
                info.show(JEConfig.getStage(), "Info", "Missing parameters", "Soem parameters are not configured");
                return false;
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SampleEditor.class.getName()).log(Level.SEVERE, null, ex);
            ExceptionDialog errDia = new ExceptionDialog();
            errDia.show(JEConfig.getStage(), "Error", "Error while exporting", "Could not write to file", ex, JEConfig.PROGRAMM_INFO);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SampleEditor.class.getName()).log(Level.SEVERE, null, ex);
            ExceptionDialog errDia = new ExceptionDialog();
            errDia.show(JEConfig.getStage(), "Error", "Error while exporting", "Unsupported encoding", ex, JEConfig.PROGRAMM_INFO);
        }
        return false;
    }

    public void buildGUI(final JEVisAttribute attribute, final List<JEVisSample> samples) {
        _isBuild = true;
        TabPane tabPane = new TabPane();
        tabPane.setMaxWidth(2000);

        String sampleHeader = "";
        if (!samples.isEmpty()) {
            DateTimeFormatter dtfDateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter timezone = DateTimeFormat.forPattern("z");
            try {
                sampleHeader = " " + dtfDateTime.print(samples.get(0).getTimestamp())
                        + " - " + dtfDateTime.print(samples.get(samples.size() - 1).getTimestamp())
                        + " " + timezone.print(samples.get(0).getTimestamp());
            } catch (JEVisException ex) {
                Logger.getLogger(SampleExportExtension.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

//        fHeader.setText(attribute.getObject().getName() + "[" + attribute.getObject().getID() + "] - " + attribute.getName());
        fHeader.setText(attribute.getObject().getName() + " " + attribute.getName() + " " + sampleHeader);

        _samples = samples;
        final ToggleGroup group = new ToggleGroup();

        bDateTime.setToggleGroup(group);
        bDateTime2.setToggleGroup(group);
        bDateTime.setSelected(true);

        fTimeFormate.setDisable(true);
        fDateFormat.setDisable(true);

        Button bValueFaormateHelp = new Button("?");

        HBox fielBox = new HBox(5d);
        HBox.setHgrow(fFile, Priority.ALWAYS);
        fFile.setPrefWidth(300);
        fielBox.getChildren().addAll(fFile, bFile);

//        bFile.setStyle("-fx-background-color: #5db7de;");
        Label lFileOrder = new Label("Field Order:");

        fHeader.setPrefColumnCount(1);
//        fHeader.setDisable(true);

        fTextArea.setPrefColumnCount(5);
        fTextArea.setPrefWidth(500d);
        fTextArea.setPrefHeight(110d);
        fTextArea.setStyle("-fx-font-size: 14;");

        GridPane gp = new GridPane();
        gp.setStyle("-fx-background-color: transparent;");
        gp.setPadding(new Insets(10));
        gp.setHgap(7);
        gp.setVgap(7);

        int y = 0;
        gp.add(lLineSep, 0, y);
        gp.add(fLineSep, 1, y);

        gp.add(lEnclosedBy, 0, ++y);
        gp.add(fEnclosedBy, 1, y);

        gp.add(lValueFormate, 0, ++y);
        gp.add(fValueFormat, 1, y);

        gp.add(new Separator(Orientation.HORIZONTAL), 0, ++y, 2, 1);

        gp.add(bDateTime, 0, ++y, 2, 1);
        gp.add(lDateTimeFormat, 0, ++y);
        gp.add(fDateTimeFormat, 1, y);

        gp.add(bDateTime2, 0, ++y, 2, 1);
        gp.add(lTimeFormate, 0, ++y);
        gp.add(fTimeFormate, 1, y);
        gp.add(lDateFormat, 0, ++y);
        gp.add(fDateFormat, 1, y);

        gp.add(new Separator(Orientation.HORIZONTAL), 0, ++y, 2, 1);

//        gp.add(new Separator(Orientation.HORIZONTAL), 0, ++y, 2, 1);
        gp.add(lHeader, 0, ++y);
        gp.add(fHeader, 1, y);

        gp.add(lFileOrder, 0, ++y);
        gp.add(buildFildOrder(), 1, y);

        gp.add(lPFilePath, 0, ++y);
        gp.add(fielBox, 1, y);

        gp.add(lExample, 0, ++y, 2, 1);
        gp.add(fTextArea, 0, ++y, 2, 1);

        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov,
                    Toggle old_toggle, Toggle new_toggle) {
                if (group.getSelectedToggle() != null) {
                    dateChanged();
                    updateOderField();
                }
            }
        });

        fEnclosedBy.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                updatePreview();
            }
        });

        fLineSep.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                updatePreview();
            }
        });

        fValueFormat.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                System.out.println("key pressed");
                try {
                    DateTimeFormat.forPattern(fDateTimeFormat.getText());
                    updatePreview();
                } catch (Exception ex) {
                    System.out.println("invalied Formate");
                }

            }
        });

        fDateTimeFormat.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                System.out.println("update linesep");
                try {
                    DateTimeFormat.forPattern(fDateTimeFormat.getText());
                    updatePreview();
                } catch (Exception ex) {
                    System.out.println("invalied Formate");
                }

            }
        });

        fDateFormat.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                System.out.println("update linesep");
                try {
                    DateTimeFormat.forPattern(fDateFormat.getText());
                    updatePreview();
                } catch (Exception ex) {
                    System.out.println("invalied Formate");
                }

            }
        });

        fTimeFormate.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                System.out.println("update linesep");
                try {
                    DateTimeFormat.forPattern(fTimeFormate.getText());
                    updatePreview();
                } catch (Exception ex) {
                    System.out.println("invalied Formate");

                }

            }
        });

        bFile.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("CSV File Destination");
                DateTimeFormatter fmtDate = DateTimeFormat.forPattern("yyyyMMdd");

                fileChooser.setInitialFileName(attribute.getObject().getName() + "_" + attribute.getName() + "_" + fmtDate.print(new DateTime()) + ".csv");
                File file = fileChooser.showSaveDialog(JEConfig.getStage());
                if (file != null) {
                    destinationFile = file;
                    fFile.setText(file.toString());
                }
            }
        });

//        export.setOnAction(new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent t) {
//                writeFile(fFile.getText(), createCSVString(Integer.MAX_VALUE));
//            }
//        });
        fHeader.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                updatePreview();
            }
        });

        updateOderField();
        updatePreview();

        ScrollPane scroll = new ScrollPane();
        scroll.setStyle("-fx-background-color: transparent");
        scroll.setMaxSize(10000, 10000);
        scroll.setContent(gp);
//        _view.getChildren().setAll(scroll);
        _view.setCenter(scroll);
//        return gp;
    }

    private void updateOderField() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (bDateTime.isSelected()) {
                    tabel.getColumns().removeAll(dateColum, timeColum, valueColum);
                    tabel.getColumns().addAll(dateTimeColumn, valueColum);
                } else {
                    tabel.getColumns().removeAll(dateTimeColumn, valueColum);
                    tabel.getColumns().addAll(dateColum, timeColum, valueColum);
                }
            }
        });

    }

    private Node buildFildOrder() {
        HBox root = new HBox();

//        String help = "Use Drag&Drop to change the oder.";
        tabel.setMaxHeight(18);
        tabel.setPlaceholder(new Region());

        dateColum.setSortable(false);
        dateColum.setCellValueFactory(new PropertyValueFactory<SampleTable.TableSample, String>("Date"));

        valueColum.setSortable(false);
        valueColum.setCellValueFactory(new PropertyValueFactory<SampleTable.TableSample, String>("Value"));

        timeColum.setSortable(false);
        timeColum.setCellValueFactory(new PropertyValueFactory<SampleTable.TableSample, String>("time"));

        dateTimeColumn.setSortable(false);
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<SampleTable.TableSample, String>("Datetime"));

        tabel.setMinWidth(555d);//TODo: replace Dirty workaround
        tabel.setPrefHeight(200d);//TODo: replace Dirty workaround
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tabel.getColumns().addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change change) {
                updatePreview();
            }
        });

        root.getChildren().add(tabel);

        return root;

    }

    private String createCSVString(int lineCount) {
        final StringBuilder sb = new StringBuilder();

        DateTimeFormatter dtfDateTime = DateTimeFormat.forPattern("yyyy");
        DateTimeFormatter dtfDate = DateTimeFormat.forPattern("yyyy");
        DateTimeFormatter dtfTime = DateTimeFormat.forPattern("yyyy");

        if (bDateTime.isSelected()) {
            dtfDateTime = DateTimeFormat.forPattern(fDateTimeFormat.getText());
        } else {
            dtfDate = DateTimeFormat.forPattern(fDateFormat.getText());
            dtfTime = DateTimeFormat.forPattern(fTimeFormate.getText());
        }

        DecimalFormat decimalFormat = new DecimalFormat(fValueFormat.getText());

        String enclosed = fEnclosedBy.getText();
        String fSeperator = fLineSep.getText();
        List<TableColumn> fieldOrder = new ArrayList<>();

        for (Object column : tabel.getColumns()) {
            fieldOrder.add((TableColumn) column);
        }

        sb.append(fHeader.getText());
        if (!fHeader.getText().isEmpty()) {
            sb.append(System.getProperty("line.separator"));
        }

        int count = 0;
        for (JEVisSample sample : _samples) {
            if (count > lineCount) {
                break;
            } else {
                count++;
            }

            try {

                for (TableColumn column : fieldOrder) {
                    sb.append(enclosed);

                    if (column.equals(valueColum)) {
                        sb.append(decimalFormat.format(sample.getValueAsDouble()));
                    } else if (column.equals(dateTimeColumn)) {
                        sb.append(dtfDateTime.print(sample.getTimestamp()));
                    } else if (column.equals(dateColum)) {
                        sb.append(dtfDate.print(sample.getTimestamp()));
                    } else if (column.equals(timeColum)) {
                        sb.append(dtfTime.print(sample.getTimestamp()));
                    }

                    sb.append(enclosed);
                    if (fieldOrder.indexOf(column) != fieldOrder.size() - 1) {
                        sb.append(fSeperator);
                    }
                }

//                sb.append(enclosed);
//                sb.append(fmtDate.print(sample.getTimestamp()));
//                sb.append(enclosed);
//
//                sb.append(fSeperator);
//
//                sb.append(enclosed);
//                sb.append(decimalFormat.format(sample.getValueAsDouble()));
//                sb.append(enclosed);
                sb.append(System.getProperty("line.separator"));

            } catch (JEVisException ex) {
                Logger.getLogger(SampleExportExtension.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }

        return sb.toString();
    }

    private void writeFile(String file, String text) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer;
//        try {
        writer = new PrintWriter(file, "UTF-8");
        writer.println(text);
        writer.close();

//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(CSVExport.class
//                    .getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(CSVExport.class
//                    .getName()).log(Level.SEVERE, null, ex);
//        }
    }

    private void dateChanged() {

        lDateTimeFormat.setDisable(bDateTime2.isSelected());
        fDateTimeFormat.setDisable(bDateTime2.isSelected());

        lTimeFormate.setDisable(bDateTime.isSelected());
        lDateFormat.setDisable(bDateTime.isSelected());
        fTimeFormate.setDisable(bDateTime.isSelected());
        fDateFormat.setDisable(bDateTime.isSelected());

    }

    private void updatePreview() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                fTextArea.setText(createCSVString(5));
            }
        });

    }

}
