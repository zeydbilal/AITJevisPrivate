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

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisSample;
import org.jevis.application.dialog.DialogHeader;
import org.jevis.application.dialog.ExceptionDialog;
import org.jevis.application.dialog.InfoDialog;
import org.jevis.jeconfig.JEConfig;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class SampleEditor {

    public static String ICON = "1415314386_Graph.png";

    public static enum Response {

        YES, CANCEL
    };

    private Response response = Response.CANCEL;

//    final Label passL = new Label("New Password:");
//    final Label confirmL = new Label("Comfirm Password:");
//    final PasswordField pass = new PasswordField();
//    final PasswordField comfirm = new PasswordField();
    final Button ok = new Button("OK");

    /**
     *
     * @param owner
     * @param attribute
     * @return
     */
    public Response show(Stage owner, final JEVisAttribute attribute) {
        final Stage stage = new Stage();

        final List<JEVisSample> samples = attribute.getAllSamples();

        stage.setTitle("Sample Editor");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);

        VBox root = new VBox();
        root.setMaxWidth(2000);

        final Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(750);
        stage.setHeight(660);
        stage.setMaxWidth(2000);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);

        HBox buttonPanel = new HBox();

        ok.setDefaultButton(true);

//        Button export = new Button("Export");
        Button cancel = new Button("Cancel");
        cancel.setCancelButton(true);

        Region spacer = new Region();
        spacer.setMaxWidth(2000);

        buttonPanel.getChildren().addAll(spacer, ok, cancel);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));
        buttonPanel.setSpacing(10);//10
        buttonPanel.setMaxHeight(25);
        HBox.setHgrow(spacer, Priority.ALWAYS);
//        HBox.setHgrow(export, Priority.NEVER);
        HBox.setHgrow(ok, Priority.NEVER);
        HBox.setHgrow(cancel, Priority.NEVER);

        final TabPane tabPane = new TabPane();
        tabPane.setMaxWidth(2000);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
//        tabPane.getStylesheets().add(JEConfig.getResource("tabEmty.css"));

//        tabPane.lookup(".tab-pane .tab-header-area .tab-header-background").setStyle("-fx-background-color: white;");
        Tab tabEditor = new Tab();
        tabEditor.setText("Editor");
        final Tab tabGraph = new Tab();
        tabGraph.setText("Graph");

        SampleTable table = new SampleTable(samples);
        table.setPrefSize(1000, 1000);

        final Tab tabCSVExport = new Tab();
        tabCSVExport.setText("CSV Export");
        final CSVExport csvExport = new CSVExport();
        tabCSVExport.setContent(csvExport.buildGUI(attribute, samples));

        tabEditor.setContent(table);

        tabPane.getTabs().addAll(tabEditor, tabGraph, tabCSVExport);

        GridPane gp = new GridPane();
        gp.setStyle("-fx-background-color: white;");
//        gp.setPadding(new Insets(10));
        gp.setHgap(0);
        gp.setVgap(0);
        int y = 0;
        gp.add(tabPane, 0, y);
//        gp.add(pass, 1, y);
//        gp.add(confirmL, 0, ++y);
//        gp.add(comfirm, 1, y);

//        Separator sep = new Separator(Orientation.HORIZONTAL);
//        sep.setMinHeight(10);
        Node header = DialogHeader.getDialogHeader(ICON, "Sample Editor");//new Separator(Orientation.HORIZONTAL),

        root.getChildren().addAll(header, gp, buttonPanel);
        VBox.setVgrow(buttonPanel, Priority.NEVER);
        VBox.setVgrow(header, Priority.NEVER);

//        ok.setDisable(true);
        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
//                stage.close();
//                response = Response.YES;
                System.out.println("tab: ");
                if (tabPane.getSelectionModel().selectedItemProperty().getValue().equals(tabCSVExport)) {
                    System.out.println("exports");
                    try {
                        InfoDialog info = new InfoDialog();
                        if (csvExport.doExport()) {
                            info.show(JEConfig.getStage(), "Success", "Export was successful", "Export was successful");
                        } else {
                            info.show(JEConfig.getStage(), "Info", "Missing parameters", "Soem parameters are not configured");
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
                }

            }
        });

        final boolean isloaded = false;
        tabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
//                System.out.println("Tab changed: " + tabGraph.getContent());
                if (newValue.equals(1) && tabGraph.getContent() == null) {
                    JEConfig.getStage().getScene().setCursor(Cursor.WAIT);
                    scene.setCursor(Cursor.WAIT);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            LineChart<String, Number> chart = buildChart(samples);
                            final ScrollPane scroll = new ScrollPane();
                            scroll.setVisible(false);
                            scroll.setMaxWidth(2000);
                            scroll.setPrefWidth(2000);
                            tabGraph.setContent(scroll);
                            scroll.setContent(chart);
                            tabGraph.setContent(scroll);
                            scroll.setVisible(true);
                            JEConfig.getStage().getScene().setCursor(Cursor.DEFAULT);
                            scene.setCursor(Cursor.DEFAULT);
                        }
                    });

//                    LineChart<String, Number> chart = buildChart(attribute);
//
//                    final ScrollPane scroll = new ScrollPane();
//                    scroll.setMaxWidth(2000);
//                    scroll.setPrefWidth(2000);
//                    tabGraph.setContent(scroll);
//                    scroll.setContent(chart);
//                    tabGraph.setContent(scroll);
//                    JEConfig.getStage().getScene().setCursor(Cursor.DEFAULT);
                }
            }
        });

//        pass.setOnKeyReleased(new EventHandler<KeyEvent>() {
//
//            @Override
//            public void handle(KeyEvent t) {
//                checkPW();
//            }
//        });
//
//        comfirm.setOnKeyReleased(new EventHandler<KeyEvent>() {
//
//            @Override
//            public void handle(KeyEvent t) {
//                checkPW();
//            }
//        });
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                stage.close();
                response = Response.CANCEL;

            }
        });

//        pass.requestFocus();
        stage.showAndWait();
        System.out.println("return " + response);

        return response;
    }

//    public String getPassword() {
//        return pass.getText();
//    }
//
//    private void checkPW() {
//        if (!pass.getText().isEmpty() && !comfirm.getText().isEmpty()) {
//            if (pass.getText().equals(comfirm.getText())) {
//                ok.setDisable(false);
//            }
//        }
//
//    }
    private LineChart<String, Number> buildChart(List<JEVisSample> samples) {

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
//        xAxis.setLabel("Month");
        final LineChart<String, Number> lineChart
                = new LineChart<String, Number>(xAxis, yAxis);

        String titel = String.format("");

        lineChart.setTitle(titel);
//        lineChart.setAnimated(true);
        lineChart.setLegendVisible(false);
//        lineChart.setCache(true);

        XYChart.Series series1 = new XYChart.Series();
//        series1.setName(att.getName());
        DateTimeFormatter fmtDate = DateTimeFormat.forPattern("yyyy-MM-dd    HH:mm:ss");
        DateTimeFormatter fmttime = DateTimeFormat.forPattern("E HH:mm:ss");
        DateTimeFormatter fmttime2 = DateTimeFormat.forPattern("E HH:mm:ss");

        boolean isFirst = true;
        boolean isFirstMo = false;

        for (JEVisSample sample : samples) {
            try {
                String datelabel = fmtDate.print(sample.getTimestamp());
//                if (isFirst) {
//                    datelabel = fmtDate.print(sample.getTimestamp());
//                    isFirst = false;
//                } else {
//                    if (sample.getTimestamp().getDayOfWeek() == 1 && isFirstMo) {
//                        System.out.println("is monday: " + sample.getTimestamp());
//                        datelabel = fmtDate.print(sample.getTimestamp());
//                        isFirstMo = false;
//                    } else {
//                        datelabel = fmttime.print(sample.getTimestamp());
//                        isFirstMo = true;
//                    }
//
//                }
                series1.getData().add(new XYChart.Data(datelabel, sample.getValueAsDouble()));
            } catch (JEVisException ex) {
                Logger.getLogger(SampleEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        int size = 22 * samples.size();
        lineChart.setPrefWidth(size);
        lineChart.getData().addAll(series1);

        return lineChart;
    }
}
