/**
 * Copyright (C) 2014 Envidatec GmbH <info@envidatec.com>
 *
 * This file is part of JEApplication.
 *
 * JEApplication is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation in version 3.
 *
 * JEApplication is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * JEApplication. If not, see <http://www.gnu.org/licenses/>.
 *
 * JEApplication is part of the OpenJEVis project, further project information
 * are published at <http://www.OpenJEVis.org/>.
 */
package org.jevis.jeconfig.plugin.unit;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.measure.unit.Unit;
import org.jevis.api.JEVisDataSource;
import org.jevis.application.dialog.DialogHeader;
import static org.jevis.application.dialog.AboutDialog.ICON_TASKBAR;
import org.jevis.application.resource.ResourceLoader;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class UnitSelectDialog {

    public static enum Response {

        NO, YES, CANCEL
    };

    private Response response = Response.CANCEL;

    UnitTree uTree;

    public Response show(Stage owner, String title, JEVisDataSource ds) {
        final Stage stage = new Stage();

        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);

        VBox root = new VBox();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        //TODo better be dynamic

        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(true);

        ImageView imageView = ResourceLoader.getImage(ICON_TASKBAR, 65, 65);

        stage.getIcons().add(imageView.getImage());

        Node header = DialogHeader.getDialogHeader("1404313956_evolution-tasks.png", "Unit Selection");

        HBox buttonPanel = new HBox();

        Button ok = new Button("OK");
        ok.setDefaultButton(true);

        Button cancel = new Button("Cancel");
        cancel.setCancelButton(true);

        buttonPanel.getChildren().addAll(ok, cancel);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));
        buttonPanel.setSpacing(10);
        buttonPanel.setMaxHeight(25);

        uTree = new UnitTree(ds);
        uTree.setPrefSize(550, 600);

        VBox box = new VBox();
        box.getChildren().add(uTree);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        //spalte , zeile bei 0 starten
//        grid.add(uTree, 0, 0);
        root.getChildren().setAll(header, new Separator(Orientation.HORIZONTAL), box, buttonPanel);
        VBox.setVgrow(box, Priority.ALWAYS);

        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
//                System.out.println("Size: h:" + stage.getHeight() + " w:" + stage.getWidth());
                response = Response.YES;
                stage.close();
//                isOK.setValue(true);

            }
        });

        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                response = Response.CANCEL;
                stage.close();

            }
        });

        stage.setWidth(365);
        //Workaround to set a dynamic size
        stage.setHeight(768);

        stage.sizeToScene();
        stage.showAndWait();

        return response;
    }

    public Unit getUnit() {
        return uTree.getSelectedObject().getUnit();
    }

}
