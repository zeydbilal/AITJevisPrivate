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
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisSample;

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