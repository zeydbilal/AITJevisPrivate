/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.object.attribute;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
////import javafx.scene.control.Dialogs;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.jevis.jeapi.JEVisAttribute;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisSample;
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

    public FileValueEditor(JEVisAttribute att) {
        _attribute = att;
    }

    @Override
    public boolean hasChanged() {
//        System.out.println(_attribute.getName() + " changed: " + _hasChanged);
        return _hasChanged;
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
