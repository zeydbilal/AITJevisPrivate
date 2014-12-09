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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import org.jevis.api.JEVisSample;
import org.jevis.application.dialog.DialogHeader;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.tool.datepicker.DatePicker;
import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class SampleEditor {

    public static String ICON = "1415314386_Graph.png";
    private boolean _dataChanged = false;
    private SampleEditorExtension _visibleExtension = null;
    private DateTime _from = null;
    private DateTime _until = null;

    public static enum Response {

        YES, CANCEL
    };
    List<JEVisSample> samples = new ArrayList<>();

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

        stage.setTitle("Sample Editor");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);

        VBox root = new VBox();
        root.setMaxWidth(2000);

        final Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(740);
        stage.setHeight(690);
        stage.setMaxWidth(2000);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);

        HBox buttonPanel = new HBox();

        ok.setDefaultButton(true);

//        Button export = new Button("Export");
        Button cancel = new Button("Close");
        cancel.setCancelButton(true);

        Region spacer = new Region();
        spacer.setMaxWidth(2000);

        Label startLabel = new Label("From:");
        DatePicker startdate = new DatePicker();

        startdate.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
//        startdate.getCalendarView().todayButtonTextProperty().set("Today");
        startdate.getCalendarView().setShowWeeks(false);
        startdate.getStylesheets().add(JEConfig.getResource("DatePicker.css"));

        Label endLabel = new Label("Until:");
        DatePicker enddate = new DatePicker();

        enddate.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        enddate.getCalendarView().todayButtonTextProperty().set("Today");
        enddate.getCalendarView().setShowWeeks(true);
        enddate.getStylesheets().add(JEConfig.getResource("DatePicker.css"));

        SampleTabelExtension tabelExtension = new SampleTabelExtension(attribute);//Default plugin

//        final List<JEVisSample> samples = attribute.getAllSamples();
        if (attribute.hasSample()) {
            _from = attribute.getTimestampFromLastSample().minus(Duration.standardDays(1));
            _until = attribute.getTimestampFromLastSample();

            startdate = new DatePicker(Locale.getDefault(), _from.toDate());
            startdate.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            startdate.setSelectedDate(_from.toDate());
            startdate.getCalendarView().setShowWeeks(false);
            startdate.getStylesheets().add(JEConfig.getResource("DatePicker.css"));

//            enddate.setSelectedDate(_until.toDate());
            enddate.selectedDateProperty().setValue(_until.toDate());
            enddate.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            enddate.setSelectedDate(_from.toDate());
            enddate.getCalendarView().setShowWeeks(true);
            enddate.getStylesheets().add(JEConfig.getResource("DatePicker.css"));

        }

        buttonPanel.getChildren().addAll(startLabel, startdate, endLabel, enddate, spacer, ok, cancel);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));
        buttonPanel.setSpacing(10);//10
        buttonPanel.setMaxHeight(25);
        HBox.setHgrow(spacer, Priority.ALWAYS);
//        HBox.setHgrow(export, Priority.NEVER);
        HBox.setHgrow(ok, Priority.NEVER);
        HBox.setHgrow(cancel, Priority.NEVER);

        final List<SampleEditorExtension> extensions = new ArrayList<>();

        extensions.add(tabelExtension);
        extensions.add(new SampleGraphExtension(attribute));
        extensions.add(new SampleExportExtension(attribute));

        final List<Tab> tabs = new ArrayList<>();

//        boolean fistEx = true;
        for (SampleEditorExtension ex : extensions) {
//            _dataChanged
//            if (fistEx) {
//                System.out.println("is first");
//                ex.setSamples(attribute, samples);
//                ex.update();
//                fistEx = false;
//            }

            Tab tabEditor = new Tab();
            tabEditor.setText(ex.getTitel());
            tabEditor.setContent(ex.getView());
            tabs.add(tabEditor);

        }
        _visibleExtension = extensions.get(0);
        updateSamples(attribute, _from, _until, extensions);

        final TabPane tabPane = new TabPane();
//        tabPane.setMaxWidth(2000);
//        tabPane.setMaxHeight(2000);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(tabs);

//        tabPane.setPrefSize(200, 200);
//        tabPane.getSelectionModel().selectFirst();
        GridPane gp = new GridPane();
        gp.setStyle("-fx-background-color: white;");

        gp.setHgap(0);
        gp.setVgap(0);
        int y = 0;
        gp.add(tabPane, 0, y);

        Node header = DialogHeader.getDialogHeader(ICON, "Sample Editor");//new Separator(Orientation.HORIZONTAL),

        root.getChildren().addAll(header, gp, buttonPanel);
        VBox.setVgrow(buttonPanel, Priority.NEVER);
        VBox.setVgrow(header, Priority.NEVER);

//        ok.setDisable(true);
        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                System.out.println("OK action to: " + _visibleExtension.getTitel());
                _visibleExtension.sendOKAction();
            }
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

            @Override
            public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
//                System.out.println("tabPane.getSelectionModel(): " + t1.getText());

                for (SampleEditorExtension ex : extensions) {
                    if (ex.getTitel().equals(t1.getText())) {
                        ex.update();
                        _visibleExtension = ex;
                    }
                }
//                }
            }
        });

        startdate.selectedDateProperty().addListener(new ChangeListener<Date>() {

            @Override
            public void changed(ObservableValue<? extends Date> ov, Date t, Date t1) {
                DateTime from = new DateTime(t1.getTime());
                _from = from;
//                _visibleExtension.setSamples(attribute, attribute.getSamples(_from, _until));
                updateSamples(attribute, _from, _until, extensions);
            }
        });

        enddate.selectedDateProperty().addListener(new ChangeListener<Date>() {

            @Override
            public void changed(ObservableValue<? extends Date> ov, Date t, Date t1) {
                DateTime until = new DateTime(t1.getTime());
                _until = until;
//                _visibleExtension.setSamples(attribute, attribute.getSamples(_from, _until));
                updateSamples(attribute, _from, _until, extensions);
            }
        });

        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                stage.close();
                response = Response.CANCEL;

            }
        });

        //TODO: replace Workaround.., without it the first tab will be emty 
//        tabPane.getSelectionModel().selectLast();
//        tabPane.getSelectionModel().selectFirst();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tabPane.getSelectionModel().selectLast();
                tabPane.getSelectionModel().selectFirst();
            }
        });

        stage.showAndWait();

        return response;
    }

    /**
     *
     * @param att
     * @param from
     * @param until
     * @param extensions
     */
    private void updateSamples(final JEVisAttribute att, final DateTime from, final DateTime until, List<SampleEditorExtension> extensions) {
        samples.clear();
        samples.addAll(att.getSamples(from, until));
        for (SampleEditorExtension ex : extensions) {
            ex.setSamples(att, samples);
        }
        _dataChanged = true;
        _visibleExtension.update();

    }

}
