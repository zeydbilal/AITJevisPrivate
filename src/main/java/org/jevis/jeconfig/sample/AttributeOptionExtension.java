/**
 * Copyright (C) 2015 Envidatec GmbH <info@envidatec.com>
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.BorderPane;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisOption;
import org.jevis.api.JEVisSample;
import org.jevis.commons.config.BasicOption;

/**
 * Attribute editor extension to configure JEVIsOptions in an generic way.
 *
 * @author Florian Simon
 */
public class AttributeOptionExtension implements SampleEditorExtension {

    private BorderPane _view = new BorderPane();
    private JEVisAttribute _att = null;

    public AttributeOptionExtension(JEVisAttribute att) {
        _att = att;
        update();
    }

    private void addContexMenu(TreeTableView<JEVisOption> treeview) {
        ContextMenu menu = new ContextMenu();

        MenuItem removeMenuItem = new MenuItem("Remove Option");
        MenuItem addMenuItem = new MenuItem("Add new Option");
        menu.getItems().addAll(addMenuItem, removeMenuItem);

        addMenuItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    TreeItem<JEVisOption> treeItem = treeview.getSelectionModel().getSelectedItem();
                    JEVisOption newOption = new BasicOption();
                    newOption.setKey("New Option");

                    TreeItem<JEVisOption> newItem = new TreeItem<>(newOption);
                    treeItem.getChildren().add(newItem);
                } catch (Exception ex) {
                    System.out.println("Error while deleting option: " + ex);
                }
            }
        });

        removeMenuItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                for (TreeItem<JEVisOption> treeItem : treeview.getSelectionModel().getSelectedItems()) {
                    try {
                        if (!treeItem.equals(treeview.getRoot())) {
                            treeItem.getParent().getChildren().remove(treeItem);
                        }
                    } catch (Exception ex) {
                        System.out.println("Error while adding option: " + ex);
                    }

                }
            }
        });

        treeview.setContextMenu(menu);

    }

    private void buildView() {
        _view = new BorderPane();

        TreeTableView<JEVisOption> tree = new TreeTableView<>();

        TreeTableColumn<JEVisOption, String> optionColumn = new TreeTableColumn<>("Option");
        tree.getColumns().add(optionColumn);
        optionColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getKey()));
        optionColumn.setEditable(true);
        optionColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        optionColumn.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<JEVisOption, String>>() {

            @Override
            public void handle(TreeTableColumn.CellEditEvent<JEVisOption, String> event) {
                final JEVisOption item = event.getRowValue().getValue();
                item.setKey(event.getNewValue());
            }
        });

        TreeTableColumn<JEVisOption, String> valueColumn = new TreeTableColumn<>("Value");
        tree.getColumns().add(valueColumn);
        valueColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getValue()));
        valueColumn.setEditable(true);
        valueColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        valueColumn.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<JEVisOption, String>>() {

            @Override
            public void handle(TreeTableColumn.CellEditEvent<JEVisOption, String> event) {
                final JEVisOption item = event.getRowValue().getValue();
                item.setValue(event.getNewValue());
            }
        });

        TreeTableColumn<JEVisOption, String> descriptColumn = new TreeTableColumn<>("Description");
        tree.getColumns().add(descriptColumn);
        descriptColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getDescription()));

        tree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        TreeTableColumn<JEVisOption, String> ccountColumn = new TreeTableColumn<>("Children");
        //Disabled for the moment
//        tree.getColumns().add(ccountColumn);
//        ccountColumn.setCellValueFactory(param -> new ReadOnlyObjectProperty<Integer>(param.getValue().getValue().getChildren().size())   );

        ccountColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<JEVisOption, String> p) -> {
            try {
                if (p != null && p.getValue() != null && p.getValue().getValue() != null) {
                    TreeItem<JEVisOption> item = p.getValue();
                    JEVisOption selectionObject = item.getValue();

                    return new ReadOnlyObjectWrapper<String>(selectionObject.getChildren().size() + "");

                } else {
                    return new ReadOnlyObjectWrapper<String>("Emty");
                }

            } catch (Exception ex) {
                System.out.println("Error in Column Fatory: " + ex);
                return new ReadOnlyObjectWrapper<String>("Error");
            }

        });

        tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tree.setEditable(true);

        tree.setRoot(buildTreeItems(_att));

        addContexMenu(tree);

        _view.setCenter(tree);

    }

    private TreeItem<JEVisOption> buildTreeItems(JEVisAttribute att) {
//        System.out.println("buildTreeItems: " + att.getName() + "    " + att.getOptions().size());

        //we need an new fake root option for the tree, this on will be hidden
        JEVisOption rootOption = new JEVisOption() {

            private List<JEVisOption> children = new ArrayList<>();

            @Override
            public void removeOption(JEVisOption option) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public List<JEVisOption> getChildren() {
                return children;
            }

            @Override
            public JEVisOption getOption(String optionName) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean hasOption(String optionName) {
                return !children.isEmpty();
            }

            @Override
            public void addOption(JEVisOption option, boolean overwrite) {
                //TODO: check if this oprion allready exists?
                children.add(option);
            }

            @Override
            public String getValue() {
                return "";
            }

            @Override
            public void setValue(String value) {
            }

            @Override
            public String getKey() {
                return "Options";
            }

            @Override
            public void setKey(String key) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean isRequired() {
                return false;
            }

            @Override
            public void setRequired(boolean required) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String getDescription() {
                return "Fake root element to show a tree hierarchy";
            }

            @Override
            public void setDescription(String description) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        for (JEVisOption opt : att.getOptions()) {
//            System.out.println("add new option to fake: " + opt.getKey());
            rootOption.addOption(opt, true);
//            fakeRoot.getValue().addChildren(opt, true);
        }

        TreeItem<JEVisOption> fakeRoot = new OptionTreeItem(rootOption);

        fakeRoot.setExpanded(true);

        return fakeRoot;

    }

    @Override
    public boolean isForAttribute(JEVisAttribute obj) {
        return true;
    }

    @Override
    public Node getView() {
        return _view;
    }

    @Override
    public String getTitel() {
        return "Options";
    }

    @Override
    public void setSamples(JEVisAttribute att, List<JEVisSample> samples) {
        //Nothing to do
    }

    @Override
    public void update() {
        System.out.println("update tree");
        buildView();
    }

    @Override
    public boolean sendOKAction() {
        try {
            _att.commit();
        } catch (JEVisException ex) {
            Logger.getLogger(AttributeOptionExtension.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

}
