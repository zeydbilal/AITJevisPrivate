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
package org.jevis.jeconfig.sample;

import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jevis.api.JEVisSample;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class SampleTable extends TableView {

    static final DateTimeFormatter fmtDate = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss Z");

    public SampleTable(List<JEVisSample> samples) {
        super();
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setPlaceholder(new Label("No Data"));

        TableColumn tsColum = new TableColumn("Timestamp");
        tsColum.setCellValueFactory(new PropertyValueFactory<TableSample, String>("Date"));

        TableColumn valueColum = new TableColumn("Value");
        valueColum.setCellValueFactory(new PropertyValueFactory<TableSample, Double>("Value"));

        TableColumn noteColum = new TableColumn("Note");
        noteColum.setCellValueFactory(new PropertyValueFactory<TableSample, String>("Note"));

        setMinWidth(555d);//TODo: replace Dirty workaround
        setPrefHeight(200d);//TODo: replace Dirty workaround
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        getColumns().addAll(tsColum, valueColum, noteColum);

        List<TableSample> tjc = new LinkedList<>();
        for (JEVisSample sample : samples) {
            tjc.add(new TableSample(sample));
        }

        final ObservableList<TableSample> data = FXCollections.observableArrayList(tjc);
        setItems(data);

    }

    public class TableSample {

        private SimpleStringProperty date = new SimpleStringProperty("Error");
        private SimpleDoubleProperty value = new SimpleDoubleProperty(0d);
        private SimpleStringProperty note = new SimpleStringProperty("Error");

        private JEVisSample _sample = null;

        /**
         *
         * @param relation
         * @param jclass
         */
        public TableSample(JEVisSample sample) {
            try {
                this.date = new SimpleStringProperty(fmtDate.print(sample.getTimestamp()));
                this.value = new SimpleDoubleProperty(sample.getValueAsDouble());
                this.note = new SimpleStringProperty(sample.getNote());
                _sample = sample;
            } catch (Exception ex) {
            }
        }

        public JEVisSample getSample() {
            return _sample;
        }

        public String getDate() {
            return date.get();
        }

        public void setDate(String fName) {
            date.set(fName);
        }

        public String getNote() {
            return note.get();
        }

        public void setNote(String noteString) {
            note.set(noteString);
        }

        public Double getValue() {
            return value.get();
        }

        public void setValue(Double fName) {
            value.set(fName);
        }
    }
}
