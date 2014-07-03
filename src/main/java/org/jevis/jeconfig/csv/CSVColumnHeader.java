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
package org.jevis.jeconfig.csv;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jevis.api.JEVisAttribute;
import org.jevis.application.dialog.SelectTargetDialog;
import org.jevis.application.object.tree.UserSelection;
import org.jevis.application.resource.ResourceLoader;
import org.jevis.jeconfig.JEConfig;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeParser;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class CSVColumnHeader {

    private VBox root = new VBox(5);
    private ComboBox<String> timeZone;
    private TextField formate = new TextField();
    private Label timeZoneL = new Label("TimeZone:");
    private Label targetL = new Label("Target:");
    private Button targetButton = new Button("Choose..");

    Label typeL = new Label("Meaning:");
    Label formateL = new Label("Formate:");
    JEVisAttribute _target = null;
    private ComboBox<String> meaning;
    private RadioButton value = new RadioButton("Value");
    private RadioButton date = new RadioButton("Date");
    private ToggleGroup group = new ToggleGroup();
    private HashMap<Integer, CSVLine> _lines = new HashMap<Integer, CSVLine>();
    private HashMap<Integer, SimpleObjectProperty<Node>> _valuePropertys = new HashMap<Integer, SimpleObjectProperty<Node>>();
    private HashMap<Integer, CSVCellGraphic> _valueGraphic = new HashMap<Integer, CSVCellGraphic>();

    private CSVTable _table;

    public static enum Meaning {

        Ignore, Date, DateTime, Time, Value, Text, Index
    };

    private Meaning currentMeaning = Meaning.Ignore;
    private int coloumNr = -1;

    public CSVColumnHeader(CSVTable table, int column) {
        coloumNr = column;
        _table = table;
        buildGraphic();
    }

    public int getColumn() {
        return coloumNr;
    }

    public JEVisAttribute getTarget() {
        return _target;
    }

    private String getCurrentFormate() {
        return formate.getText();
    }

    public SimpleObjectProperty getValueProperty(CSVLine line) {
        int lineNumber = line.getRowNumber();
        if (_valuePropertys.containsKey(lineNumber)) {
            return _valuePropertys.get(lineNumber);
        } else {
            _lines.put(lineNumber, line);

            CSVCellGraphic graphic = new CSVCellGraphic(line.getColumn(coloumNr));
            _valueGraphic.put(lineNumber, graphic);
            graphic.setText(getFormatedValue(line.getColumn(coloumNr)));
            graphic.setValid(valueIsValid(line.getColumn(coloumNr)));

            if (getMeaning() == Meaning.Ignore) {
                graphic.getGraphic().setDisable(true);
            }

            _valuePropertys.put(lineNumber, new SimpleObjectProperty<>(graphic.getGraphic()));
            return _valuePropertys.get(lineNumber);
        }
    }

    public String getFormatedValue(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(getCurrentFormate());

            switch (currentMeaning) {
                case Date:

                    Date date = sdf.parse(value);
                    return sdf.format(date);
                case DateTime:
                    Date datetime = sdf.parse(value);
                    return sdf.format(datetime);
                case Time:
                    Date time = sdf.parse(value);
                    return sdf.format(time);
                case Value:
                    DecimalFormat df = new DecimalFormat(getCurrentFormate());
                    Number number = df.parse(value);
                    Double dValue = number.doubleValue();

                    return df.format(dValue);
                case Index:
                    break;
                case Ignore:
//                    System.out.println("To Ignore");
            }
        } catch (ParseException pe) {
            return value;
        }
        return value;
    }

    /**
     *
     * @param value
     * @return
     * @throws ParseException
     */
    public double getDoubleValue(String value) throws ParseException {
        if (getMeaning() == Meaning.Value) {
            DecimalFormat df = new DecimalFormat(getCurrentFormate());
            Number number = df.parse(value);
            return number.doubleValue();
        } else {
            throw new ParseException(value, coloumNr);
        }
    }

    /**
     *
     * @param value
     * @return
     * @throws ParseException
     */
    public String getTextValue(String value) throws ParseException {
        //TODO: mybee
        if (getMeaning() == Meaning.Value) {
            return value;
        } else {
            throw new ParseException(value, coloumNr);
        }
    }

    /**
     *
     * @param value
     * @return
     * @throws ParseException
     */
    public DateTime getValueAsDate(String value) throws ParseException {
        if (getMeaning() == Meaning.Date || getMeaning() == Meaning.DateTime || getMeaning() == Meaning.Time) {
            SimpleDateFormat sdf = new SimpleDateFormat(getCurrentFormate());
            Date datetime = sdf.parse(value);
            datetime.getTime();

            //TODO is this right? i think its now thred save
            DateTimeZone.setDefault(DateTimeZone.forTimeZone(getTimeZone()));
//            DateTimeParser dtp = DateTimeFormat.forPattern(getCurrentFormate()).getParser();

            return DateTimeFormat.forPattern(getCurrentFormate()).parseDateTime(value);

        } else {
            throw new ParseException(value, coloumNr);
        }
    }

    /**
     * TODO replace checks with the later uses functions like getValueAsDate
     *
     * @param value
     * @return
     */
    public boolean valueIsValid(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(getCurrentFormate());

            switch (currentMeaning) {
                case Date:
                    Date date = sdf.parse(value);
                    date.getTime();
                    return true;
                case DateTime:
                    Date datetime = sdf.parse(value);
                    datetime.getTime();
                    return true;
                case Time:
                    Date time = sdf.parse(value);
                    time.getTime();
                    return true;
                case Value:
                    DecimalFormat df = new DecimalFormat(getCurrentFormate());
                    Number number = df.parse(value);
                    Double dValue = number.doubleValue();

                    return true;
                case Text:
                    //TODO maybe check for .... if the attriute is from type string
                    return true;
                case Index:
                    return true;
                case Ignore:
                    return true;
            }
        } catch (Exception pe) {
            return false;
        }
        return false;
    }

    public void formteAllRows() {
        _table.setScrollBottom();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _table.setScrollBottom();
            }
        });

        Iterator it = _valuePropertys.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
//            System.out.println(pairs.getKey() + " = " + pairs.getValue());

            SimpleObjectProperty prop = (SimpleObjectProperty) pairs.getValue();
            CSVCellGraphic graphic = _valueGraphic.get((Integer) pairs.getKey());
            CSVLine csvLIne = _lines.get((Integer) pairs.getKey());

            graphic.setText(getFormatedValue(csvLIne.getColumn(coloumNr)));
            graphic.setValid(valueIsValid(csvLIne.getColumn(coloumNr)));

            if (getMeaning() == Meaning.Ignore) {
                graphic.getGraphic().setDisable(true);
            } else {
                graphic.getGraphic().setDisable(false);
            }

            prop.setValue(graphic.getGraphic());

        }

        _table.setLastScrollPosition();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _table.setLastScrollPosition();
            }
        });

    }

    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone(timeZone.getSelectionModel().getSelectedItem());
    }

    public Meaning getMeaning() {
        return currentMeaning;
    }

    private void setMeaning(Meaning meaning) {
        currentMeaning = meaning;

        timeZoneL.setDisable(true);
        timeZone.setDisable(true);
        formate.setDisable(true);
        formateL.setDisable(true);
        targetL.setDisable(true);
        targetButton.setDisable(true);

        switch (meaning) {
            case Date:
                System.out.println("is Date");
                timeZoneL.setDisable(false);
                timeZone.setDisable(false);
                formate.setDisable(false);
                formateL.setDisable(false);
                formate.setText("yyyy-MM-dd");
                timeZoneL.setDisable(false);
                timeZone.setDisable(false);

                break;
            case DateTime:
                System.out.println("is DateTime");
                formate.setDisable(false);
                formateL.setDisable(false);
                formate.setText("yyyy-MM-dd HH:mm:ss");
                timeZoneL.setDisable(false);
                timeZone.setDisable(false);

                break;
            case Time:
                System.out.println("is Time");
                formate.setDisable(false);
                formateL.setDisable(false);
                formate.setText("HH:mm:ss");
                timeZoneL.setDisable(false);
                timeZone.setDisable(false);
                break;
            case Value:
                System.out.println("is Value");
                formate.setDisable(false);
                formateL.setDisable(false);
                targetL.setDisable(false);
                targetButton.setDisable(false);
                formate.setText("#.#");
                break;
            case Index:
                System.out.println("is Index");
                formate.setText("#");
                break;
            case Ignore:
//                System.out.println("To Ignore");

        }

        formteAllRows();
    }

    private void buildGraphic() {
        root.setPadding(new Insets(8, 8, 8, 8));

        ObservableList<String> options = FXCollections.observableArrayList();

        for (Meaning meaningEnum : Meaning.values()) {
            options.add(meaningEnum.name());
        }

        meaning = new ComboBox<String>(options);

        meaning.getSelectionModel().selectFirst();

        formate.setPromptText("Formate");

        ObservableList<String> timeZoneOpt = FXCollections.observableArrayList();
        String[] allTimeZones = TimeZone.getAvailableIDs();

        timeZoneOpt = FXCollections.observableArrayList(allTimeZones);
        timeZone = new ComboBox<String>(timeZoneOpt);
//        timeZone.getSelectionModel().select("UTC");
        timeZone.getSelectionModel().select(TimeZone.getDefault().getID());

        timeZoneL.setDisable(true);
        timeZone.setDisable(true);

        formate.setText("#.#");

        formate.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                formteAllRows();
            }
        });

        meaning.valueProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {

                if (t1 != null) {
                    setMeaning(Meaning.valueOf(t1));
                }

            }
        });

        value.setToggleGroup(group);
        date.setToggleGroup(group);
        group.selectToggle(value);

        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                if (group.getSelectedToggle().equals(date)) {
                    timeZoneL.setDisable(false);
                    timeZone.setDisable(false);

                    formate.setText("yyyy-MM-dd HH:mm:ss");
                } else if (group.getSelectedToggle().equals(value)) {
                    timeZoneL.setDisable(true);
                    timeZone.setDisable(true);
                    formate.setText("#.#");
                }
            }
        });

        HBox boxType = new HBox(10);
        boxType.getChildren().setAll(value, date);
        HBox boxFormate = new HBox(5);
        ImageView help = JEConfig.getImage("1404161580_help_blue.png", 22, 22);
        boxFormate.getChildren().setAll(formate, help);

        help.setStyle("-fx-background-color: \n"
                + "        rgba(0,0,0,0.08);\n"
                + "    -fx-background-insets: 0 0 -1 0,0,1;\n"
                //                + "    -fx-background-radius: 5,5,4;\n"
                //                + "    -fx-padding: 3 30 3 30;\n"
                + "    -fx-text-fill: #242d35;\n"
                + "    -fx-font-size: 14px;");

        help.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                showHelp();
            }
        });

        //Damn workaround for fu***** layouts
        typeL.setPrefWidth(110);
        boxFormate.setPrefWidth(230);
        boxType.setPrefWidth(boxFormate.getPrefWidth());
        meaning.setPrefWidth(boxFormate.getPrefWidth());
        timeZone.setPrefWidth(boxFormate.getPrefWidth());
        targetButton.setPrefWidth(boxFormate.getPrefWidth());
        targetButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                SelectTargetDialog dia = new SelectTargetDialog();
                if (dia.show(JEConfig.getStage(), _table.getDataSource()) == SelectTargetDialog.Response.OK) {
                    System.out.println("OK");
                    for (UserSelection selection : dia.getUserSelection()) {
                        targetButton.setText(selection.getSelectedAttribute().getObject().getName() + "." + selection.getSelectedAttribute().getName());
                        _target = selection.getSelectedAttribute();
                    }
                }
            }
        });

        //because ingore is default
        formate.setDisable(true);
        formateL.setDisable(true);
        targetButton.setDisable(true);
        targetL.setDisable(true);

        GridPane gp = new GridPane();
        gp.setHgap(5);
        gp.setVgap(5);
        root.getChildren().add(gp);

        //x , y
        gp.add(typeL, 0, 0);
        gp.add(meaning, 1, 0);

        gp.add(formateL, 0, 1);
        gp.add(boxFormate, 1, 1);

        gp.add(timeZoneL, 0, 2);
        gp.add(timeZone, 1, 2);

        gp.add(targetL, 0, 3);
        gp.add(targetButton, 1, 3);

    }

    public Node getGraphic() {
        return root;
    }

    public void showHelp() {
        final Stage stage = new Stage();

        stage.setTitle("Help: Formate");
        stage.initModality(Modality.NONE);
        stage.initOwner(JEConfig.getStage());

//        BorderPane root = new BorderPane();
        VBox root = new VBox();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(620);
        stage.setHeight(620);
        stage.initStyle(StageStyle.UTILITY);

        BorderPane header = new BorderPane();
        header.setStyle("-fx-background-color: linear-gradient(#e2e2e2,#eeeeee);");
        header.setPadding(new Insets(10, 10, 10, 10));

        Label topTitle = new Label("Help: Formate");
        topTitle.setTextFill(Color.web("#0076a3"));
        topTitle.setFont(Font.font("Cambria", 25));

        ImageView imageView = ResourceLoader.getImage("1404161580_help_blue.png", 65, 65);

        stage.getIcons().add(imageView.getImage());

        VBox vboxLeft = new VBox();
        VBox vboxRight = new VBox();
        vboxLeft.getChildren().add(topTitle);
        vboxLeft.setAlignment(Pos.CENTER_LEFT);
        vboxRight.setAlignment(Pos.CENTER_LEFT);
        vboxRight.getChildren().add(imageView);

        header.setLeft(vboxLeft);

        header.setRight(vboxRight);

        WebView helpView = new WebView();
        helpView.getEngine().loadContent(getFormateHelpText());
//        TextArea helpText = new TextArea();
//        helpText.setText(ICON_QUESTION);

        HBox buttonbox = new HBox();
        buttonbox.setAlignment(Pos.BOTTOM_RIGHT);

        Button close = new Button("Close");
        close.setDefaultButton(true);
        close.setCancelButton(true);
        close.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                stage.hide();
            }
        });
        buttonbox.getChildren().setAll(close);
        buttonbox.setPadding(new Insets(10));

        root.getChildren().setAll(header, helpView, buttonbox);

        stage.show();
    }

    private String getFormateHelpText() {
        return "<html lang=\"en\"><head>\n"
                + "<meta http-equiv=\"content-type\" content=\"text/html; charset=windows-1252\">\n"
                + "<title>MaskFormatter (Java Platform SE 7 )</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "\n"
                + "<br>\n"
                + "<p>\n"
                + "<pre>Formate Mask</span>\n"
                + "MaskFormatter is used to format and edit strings. The behavior\n"
                + " of a <code>MaskFormatter</code> is controlled by way of a String mask\n"
                + " that specifies the valid characters that can be contained at a particular\n"
                + " location in the <code>Document</code> model. The following characters can\n"
                + " be specified:\n"
                + "\n"
                + " <table summary=\"Valid characters and their descriptions\" border=\"1\">\n"
                + " <tbody><tr>\n"
                + "    <th>Character&nbsp;</th>\n"
                + "    <th><p align=\"left\">Description</p></th>\n"
                + " </tr>\n"
                + " <tr>\n"
                + "    <td>#</td>\n"
                + "    <td>Any valid number, uses <code>Character.isDigit</code>.</td>\n"
                + " </tr>\n"
                + " <tr>\n"
                + "    <td>'</td>\n"
                + "    <td>Escape character, used to escape any of the\n"
                + "       special formatting characters.</td>\n"
                + " </tr>\n"
                + " <tr>\n"
                + "    <td>U</td><td>Any character (<code>Character.isLetter</code>). All\n"
                + "        lowercase letters are mapped to upper case.</td>\n"
                + " </tr>\n"
                + " <tr><td>L</td><td>Any character (<code>Character.isLetter</code>). All\n"
                + "        upper case letters are mapped to lower case.</td>\n"
                + " </tr>\n"
                + " <tr><td>A</td><td>Any character or number (<code>Character.isLetter</code>\n"
                + "       or <code>Character.isDigit</code>)</td>\n"
                + " </tr>\n"
                + " <tr><td>?</td><td>Any character\n"
                + "        (<code>Character.isLetter</code>).</td>\n"
                + " </tr>\n"
                + " <tr><td>*</td><td>Anything.</td></tr>\n"
                + " <tr><td>H</td><td>Any hex character (0-9, a-f or A-F).</td></tr>\n"
                + " </tbody></table>\n"
                + "</p>\n"
                + " \n"
                + "</body></html>";
    }

}
