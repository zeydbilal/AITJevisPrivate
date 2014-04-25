/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.tool;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.jevis.jeconfig.JEConfig;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class BorderedTitledPane extends StackPane {

    public BorderedTitledPane(String titleString, Node content) {
//        setStyle(JEConfig.getResource("borderpane.css"));
        Label title = new Label(" " + titleString + " ");
        title.getStyleClass().add("bordered-titled-title");
        StackPane.setAlignment(title, Pos.TOP_CENTER);

        StackPane contentPane = new StackPane();
        content.getStyleClass().add("bordered-titled-content");
        contentPane.getChildren().add(content);

        getStyleClass().add("bordered-titled-border");
        getChildren().addAll(title, contentPane);
    }
}
