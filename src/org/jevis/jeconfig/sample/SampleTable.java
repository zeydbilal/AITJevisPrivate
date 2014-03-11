/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.sample;

import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jevis.jeapi.JEVisAttribute;
import org.jevis.jeapi.JEVisSample;
import org.jevis.jeconfig.plugin.classes.TypeColum;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class SampleTable extends TableView {

    public SampleTable(JEVisAttribute att) {
        super();

        TableColumn tsColum = new TableColumn("Timestamp");
        tsColum.setCellValueFactory(new PropertyValueFactory<TableSample, String>("Date"));

        TableColumn valueColum = new TableColumn("Value");
        valueColum.setCellValueFactory(new PropertyValueFactory<TableSample, String>("Value"));

        setMinWidth(555d);//TODo: replace Dirty workaround
        setPrefHeight(200d);//TODo: replace Dirty workaround
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        getColumns().addAll(tsColum, valueColum);

        List<TableSample> tjc = new LinkedList<>();
        for (JEVisSample sample : att.getAllSamples()) {
            tjc.add(new TableSample(sample));
        }

        final ObservableList<TableSample> data = FXCollections.observableArrayList(tjc);
        setItems(data);

    }

    public class TableSample {

        private SimpleStringProperty date = new SimpleStringProperty("Error");
        private SimpleStringProperty value = new SimpleStringProperty("Error");

        /**
         *
         * @param relation
         * @param jclass
         */
        public TableSample(JEVisSample sample) {
            try {
                this.date = new SimpleStringProperty(sample.getTimestamp().toString());
                this.value = new SimpleStringProperty(sample.getValueAsString());
            } catch (Exception ex) {
            }
        }

        public String getDate() {
            return date.get();
        }

        public void setDate(String fName) {
            date.set(fName);
        }

        public String getValue() {
            return value.get();
        }

        public void setValue(String fName) {
            value.set(fName);
        }
    }
}
