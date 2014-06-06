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
package org.jevis.jeconfig.plugin.classes;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.tool.ImageConverter;

/**
 *
 * @author fs
 */
public class ClassTreeObject {

    private final JEVisClass _obj;
    private final HBox _view = new HBox();
    private final HBox _edior = new HBox();
    private TextField _textField = null;
    ImageView icon = new ImageView();
    Label nameLabel = new Label("*Missing*");

    public ClassTreeObject(JEVisClass obj) {
        this._obj = obj;
    }

    public Node getGraphic() {
        icon = getIcon(getObject());
        try {
            nameLabel.setText(getObject().getName());
        } catch (JEVisException ex) {
            Logger.getLogger(ClassTreeObject.class.getName()).log(Level.SEVERE, null, ex);
        }

        _view.getChildren().setAll(icon, nameLabel);

        return _view;
    }

    private void buildEditor(final TreeCell<ClassTreeObject> cell) {
        final ClassTreeObject thosOne = this;
        if (_textField == null) {
            try {
                _textField = new TextField(getObject().getName());
            } catch (JEVisException ex) {
                Logger.getLogger(ClassTreeObject.class.getName()).log(Level.SEVERE, null, ex);
            }
            _textField.setOnKeyReleased(new EventHandler<KeyEvent>() {

                @Override
                public void handle(KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        try {
                            getObject().setName(_textField.getText());
                            getObject().commit();
                            cell.commitEdit(thosOne);
                        } catch (JEVisException ex) {
                            Logger.getLogger(ClassCell.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        cell.cancelEdit();
                    }
                }
            });

        }

    }

    public Node getEditor(final TreeCell<ClassTreeObject> cell) {
        try {
            System.out.println("getCellEditor for: " + getObject().getName());
            buildEditor(cell);
            _textField.setText(getObject().getName());
            _view.getChildren().setAll(icon, _textField);

        } catch (JEVisException ex) {
            Logger.getLogger(ClassTreeObject.class.getName()).log(Level.SEVERE, null, ex);
        }

        return _view;
    }

    public JEVisClass getObject() {
        return _obj;
    }

    private ImageView getIcon(JEVisClass item) {
        try {
            if (item != null) {
                return ImageConverter.convertToImageView(item.getIcon(), 20, 20);
            } else {
                return JEConfig.getImage("1390343812_folder-open.png", 20, 20);
            }

        } catch (Exception ex) {
            System.out.println("Error while get icon for object: " + ex);
        }
        return new ImageView();

    }

    @Override
    public String toString() {

        return "TreeObject for " + getObject(); //To change body of generated methods, choose Tools | Templates.
    }

}
