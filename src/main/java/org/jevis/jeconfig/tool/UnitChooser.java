package org.jevis.jeconfig.tool;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.jevis.jeapi.JEVisAttribute;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisType;
import org.jevis.jeconfig.JEConfig;

/**
 *
 * @author fs
 */
public class UnitChooser {

    private final Stage stage;
    private Scene scene;

    public UnitChooser() {
        stage = new Stage(StageStyle.UNDECORATED);
        stage.setTitle("Unit Chooser");
        stage.getIcons().add(JEConfig.getImage("1398178225_edit-number.png"));
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    public void show(JEVisAttribute att) throws IOException {
        showSelector(att);
    }

    public void show(JEVisType type) throws IOException {
        showSelector(type);
    }

    public void showSelector(final Object att) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/fxml/UnitChooser.fxml"
                )
        );

        final Pane page = (Pane) loader.load();
        final UnitChooserControler controller = loader.<UnitChooserControler>getController();
        if (att instanceof JEVisType) {
            controller.setUnit((JEVisType) att);
        } else if (att instanceof JEVisAttribute) {

        }

//        Group rg = new Group();
        VBox box = new VBox();
        box.getChildren().add(page);
        Separator line = new Separator(Orientation.HORIZONTAL);
        VBox.setMargin(line, new Insets(10));
        box.getChildren().add(line);

        HBox hbox = new HBox();
        box.getChildren().add(hbox);

        Button ok = new Button("Accept");//Confirm
        Button cancel = new Button("Cancel");
        Region spacer = new Region();

        ok.setDefaultButton(true);

        HBox.setHgrow(ok, Priority.NEVER);
        HBox.setHgrow(cancel, Priority.NEVER);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setMargin(ok, new Insets(5, 15, 5, 5));
        HBox.setMargin(cancel, new Insets(5, 5, 5, 5));

        hbox.getChildren().addAll(spacer, cancel, ok);
        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.out.println("selected unit: " + controller.getUnit().getUnit());
                //ToDo: check if this unit is OK, maybe we have some rules
                if (att instanceof JEVisType) {
                    try {

                        ((JEVisType) att).setUnit(controller.getUnit().getUnit());
                        ((JEVisType) att).setAlternativSymbol(controller.getUnit().getAlternativSymbol());

//                    att.setUnit(controller.getUnit().getUnit());
//                    att.setAlternativSymbol(controller.getUnit().getAlternativSymbol());
                    } catch (JEVisException ex) {
                    }
                } else if (att instanceof JEVisAttribute) {
                    try {
                        //TODO: do some checks for example if selected unit ins comaptible
                        //TODO: add to UnitCooser the posibility to set the a fix Quantity

                        ((JEVisAttribute) att).setUnit(controller.getUnit().getUnit());
                        ((JEVisAttribute) att).setAlternativSymbol(controller.getUnit().getAlternativSymbol());
                    } catch (JEVisException ex) {
                    }
                }

                stage.close();
            }
        });
        ok.setDefaultButton(true);

        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                stage.close();
            }
        });

        scene = new Scene(box, 420, 240, Color.TRANSPARENT);
        stage.setScene(scene);

        //tmp test
//        test(SI.WATT.times(SI.SECOND));
//        test(SI.WATT.times(NonSI.HOUR));
        stage.showAndWait();//is modal

    }

    private void test(Unit unit) {
        System.out.println("ATTO: " + SI.ATTO(unit));
        System.out.println("CENTI: " + SI.CENTI(unit));
        System.out.println("DECI: " + SI.DECI(unit));
        System.out.println("DEKA: " + SI.DEKA(unit));
        System.out.println("EXA: " + SI.EXA(unit));
        System.out.println("FEMTO: " + SI.FEMTO(unit));
        System.out.println("GIGA: " + SI.GIGA(unit));
        System.out.println("HECTO: " + SI.HECTO(unit));
        System.out.println("KILO: " + SI.KILO(unit));
        System.out.println("MEGA: " + SI.MEGA(unit));
        System.out.println("MICRO: " + SI.MICRO(unit));
        System.out.println("MILLI: " + SI.MILLI(unit));
        System.out.println("NANO: " + SI.NANO(unit));
        System.out.println("PETA: " + SI.PETA(unit));
        System.out.println("PICO: " + SI.PICO(unit));
        System.out.println("TERA: " + SI.TERA(unit));
        System.out.println("YOCTO: " + SI.YOCTO(unit));
        System.out.println("YOTTA: " + SI.YOTTA(unit));
        System.out.println("ZEPTO: " + SI.ZEPTO(unit));
        System.out.println("ZETTA: " + SI.ZETTA(unit));

    }

}
