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
package org.jevis.jeconfig.plugin.object.attribute;

import java.io.File;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
////import javafx.scene.control.Dialogs;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisSample;
import org.jevis.jeconfig.JEConfig;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class FileValueEditor implements AttributeEditor {

    public JEVisAttribute _attribute;
    private HBox _field;
    private JEVisSample _newSample;
    private JEVisSample _lastSample;
    private boolean _hasChanged = false;
    private final BooleanProperty _changed = new SimpleBooleanProperty(false);
    private boolean _readOnly = true;

    public FileValueEditor(JEVisAttribute att) {
        _attribute = att;
    }

    @Override
    public boolean hasChanged() {
//        System.out.println(_attribute.getName() + " changed: " + _hasChanged);
        return _hasChanged;
    }

    @Override
    public BooleanProperty getValueChangedProperty() {
        return _changed;
    }

    @Override
    public void setReadOnly(boolean canRead) {
        _readOnly = canRead;
    }

//    @Override
//    public void setAttribute(JEVisAttribute att) {
//        _attribute = att;
//    }
    @Override
    public void commit() throws JEVisException {
        if (_hasChanged && _newSample != null) {

            //TODO: check if tpye is ok, maybe better at imput time
            _newSample.commit();
        }
    }

    @Override
    public Node getEditor() {
        buildTextFild();
        return _field;

    }

    private void buildTextFild() {
        if (_field == null) {
            _field = new HBox();
            Button downlaod = new Button("Download");
            Button upload = new Button("Upload");

            downlaod.setOnAction(
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(final ActionEvent e) {
                            FileChooser fileChooser = new FileChooser();
                            File file = fileChooser.showSaveDialog(JEConfig.getStage());
                            if (file != null) {
//                        try {
//                            ImageIO.write(SwingFXUtils.fromFXImage(pic.getImage(),
//                                    null), "png", file);
//                        } catch (IOException ex) {
//                            System.out.println(ex.getMessage());
//                        }
                            }
                        }
                    });

            upload.setOnAction(
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(final ActionEvent e) {
                            FileChooser fileChooser = new FileChooser();
                            File file = fileChooser.showOpenDialog(JEConfig.getStage());
                            if (file != null) {
                                //                        _newSample = _attribute.buildSample(null, e)
                            }
                        }
                    });

            upload.setDisable(_readOnly);

            _field.getChildren().addAll(downlaod, upload);

//            _field.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
//                @Override
//                public void handle(KeyEvent event) {
//                    //changed
//                    event.consume();
//
//
//
//                }
//            });
            _field.setPrefWidth(500);
            _field.setId("attributelabel");

        }
    }
}
