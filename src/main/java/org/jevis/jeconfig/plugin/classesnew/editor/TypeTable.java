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
package org.jevis.jeconfig.plugin.classesnew.editor;

import org.jevis.jeconfig.plugin.classes.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisType;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class TypeTable {

    public TypeTable() {
    }

    public GridPane buildTree(JEVisClass jclass) {
        GridPane gridPane = new GridPane();
//        gridPane.setPadding(new Insets(20, 0, 20, 20));
        gridPane.setPadding(new Insets(0, 0, 0, 0));
        gridPane.setHgap(7);
        gridPane.setVgap(7);

        TableColumn otherClassCol = new TableColumn("Attribute");
        otherClassCol.setCellValueFactory(new PropertyValueFactory<RelationshipColum, String>("otherClass"));
        TableColumn typeCol = new TableColumn("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<RelationshipColum, String>("type"));
        TableColumn directionCol = new TableColumn("GUI Type");
        directionCol.setCellValueFactory(new PropertyValueFactory<RelationshipColum, String>("direction"));

        TableView table = new TableView();
        table.setMinWidth(555d);//TODo: replace Dirty workaround
        table.setPrefHeight(200d);//TODo: replace Dirty workaround
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().addAll(otherClassCol, typeCol, directionCol);

        try {
//            final ObservableList<JEVisClassRelationship> data = FXCollections.observableArrayList(jclass.getRelationships());
            List<TypeColum> tjc = new LinkedList<>();
            for (JEVisType rel : jclass.getTypes()) {
                System.out.println("add Types: " + rel);
                tjc.add(new TypeColum(rel));
            }

            final ObservableList<TypeColum> data = FXCollections.observableArrayList(tjc);
            table.setItems(data);

        } catch (JEVisException ex) {
            Logger.getLogger(ClassRelationshipTable.class.getName()).log(Level.SEVERE, null, ex);
        }

        gridPane.add(table, 0, 0);
        gridPane.setHgrow(table, Priority.ALWAYS);

        return gridPane;
    }
}
