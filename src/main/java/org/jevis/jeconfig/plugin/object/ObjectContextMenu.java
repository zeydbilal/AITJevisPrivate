/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.object;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import org.jevis.jeapi.JEVisClass;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisObject;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.tool.ImageConverter;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ObjectContextMenu extends ContextMenu {

    private JEVisObject _obj;
    private ObjectItem _item;
    private TreeView _tree;

    public ObjectContextMenu(JEVisObject obj, ObjectItem item, TreeView tree) {
        super();
        _obj = obj;
        _item = item;
        _tree = tree;

        getItems().add(buildMenuNew());




        getItems().add(new SeparatorMenuItem());
        getItems().add(buildDelete());
        getItems().add(buildRename());
        getItems().add(buildProperties());


    }

    public List<MenuItem> buildMenuNewContent() {
        List<MenuItem> newContent = new ArrayList<>();
        try {
            for (JEVisClass jlass : _obj.getAllowedChildrenClasses()) {
                MenuItem classItem;

                classItem = new CheckMenuItem(jlass.getName(), getIcon(jlass));
                classItem.setOnAction(new NewObjectEventHandler(_tree, _item, _obj, jlass));
                newContent.add(classItem);
            }
        } catch (JEVisException ex) {
            Logger.getLogger(ObjectContextMenu.class.getName()).log(Level.SEVERE, null, ex);
        }


        return newContent;
    }

    public Menu buildMenuNew() {
        Menu addMenu = new Menu("New", JEConfig.getImage("list-add.png", 20, 20));
        addMenu.getItems().addAll(buildMenuNewContent());


        return addMenu;

    }

    private ImageView getIcon(JEVisClass jclass) {
        try {
            return ImageConverter.convertToImageView(jclass.getIcon(), 20, 20);
        } catch (Exception ex) {
            return JEConfig.getImage("1393615831_unknown2.png", 20, 20);
        }
    }

    private MenuItem buildProperties() {
        MenuItem menu = new MenuItem("Expand");//shoud be edit but i use it for expand for the time
        menu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
//                PopOver popup = new PopOver(new HBox());
//                popup.show(_item.getGraphic(), 200d, 200d, Duration.seconds(1));
                //TMP test
                System.out.println("expand all");
                _item.expandAll(true);
            }
        });
        return menu;
    }

    private MenuItem buildRename() {
        MenuItem menu = new MenuItem("Rename");
        menu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

                _tree.setEditable(true);
                _tree.edit(_item);
                _tree.setEditable(false);
            }
        });
        return menu;
    }

    private MenuItem buildDelete() {
        MenuItem menu = new MenuItem("Delete", JEConfig.getImage("list-remove.png", 20, 20));
        menu.setOnAction(new DeleteObjectEventHandler(_tree, _item, _obj));
        return menu;
    }
}
