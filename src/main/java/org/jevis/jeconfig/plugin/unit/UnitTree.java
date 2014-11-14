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
package org.jevis.jeconfig.plugin.unit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.commons.unit.UnitManager;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class UnitTree extends TreeView<UnitObject> {

    private UnitEditor _editor = new UnitEditor();
    private JEVisDataSource _ds;

    private HashMap<String, TreeItem<UnitObject>> _itemCache;
    private HashMap<String, UnitGraphic> _graphicCache;
    private HashMap<TreeItem<UnitObject>, ObservableList<TreeItem<UnitObject>>> _itemChildren;
    private ObservableList<TreeItem<UnitObject>> _emtyList = FXCollections.emptyObservableList();

    private UnitObject _dragObj;

    public UnitTree() {

    }

    public UnitTree(JEVisDataSource ds) {
        super();
        try {
            _ds = ds;
            _itemCache = new HashMap<>();
            _graphicCache = new HashMap<>();
            _itemChildren = new HashMap<>();
//            setStyle("-fx-background-color: white;");
            setMaxHeight(2014);

            UnitObject uo = new UnitObject(UnitObject.Type.FakeRoot, Unit.ONE, "Unit");
            TreeItem<UnitObject> rootItem = buildItem(uo);

            setShowRoot(false);

            getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<UnitObject>>() {

                @Override
                public void changed(ObservableValue<? extends TreeItem<UnitObject>> ov, TreeItem<UnitObject> t, TreeItem<UnitObject> t1) {
                    if (t != null) {
                        _editor.setUnit(t1.getValue());
                    }
//                    try {
//                        if (t1.getValue().getJEVisClass().getName().equals(CommonClasses.LINK.NAME)) {
//                            System.out.println("changed: oh object is a link so im loading the linked object");
//                            _editor.setObject(t1.getValue().getLinkedObject());
//                        } else {
//                            _editor.setObject(t1.getValue());
//                        }
//
//                    } catch (Exception ex) {
//                    }
                }
            });

            setCellFactory(new Callback<TreeView<UnitObject>, TreeCell<UnitObject>>() {
                @Override
                public TreeCell<UnitObject> call(TreeView<UnitObject> p) {
                    return new ObjectCell();
                }
            });

//            final KeyCombination copyID = new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN);
//            final KeyCombination copyObj = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
//            final KeyCombination add = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
//            final KeyCombination rename = new KeyCodeCombination(KeyCode.F2);
//            final KeyCombination delete = new KeyCodeCombination(KeyCode.DELETE);
//
//            addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
//
//                @Override
//                public void handle(KeyEvent t) {
//
//                    try {
//                        Unit selectedObject = getSelectionModel().getSelectedItem().getValue();
//
//                        if (copyID.match(t)) {
//                            System.out.println("Copy ID");
//                            Clipboard clip = Clipboard.getSystemClipboard();
//                            ClipboardContent content = new ClipboardContent();
//
//                            content.putString(getSelectionModel().getSelectedItem().getValue().getID().toString());
//                            clip.setContent(content);
//                            t.consume();
//                        } else if (rename.match(t)) {
//                            System.out.println("F2 rename event");
//                            Platform.runLater(new Runnable() {
//                                @Override
//                                public void run() {
//                                    fireEventRename();
//                                }
//                            });
//                            t.consume();
//
//                        } else if (delete.match(t)) {
//                            fireDelete(getSelectedObject());
//                            t.consume();
//                        } else if (copyObj.match(t)) {
//                            System.out.println("Copy Object");
//                            Clipboard clip = Clipboard.getSystemClipboard();
//                            ClipboardContent content = new ClipboardContent();
//                            Unit obj = getSelectionModel().getSelectedItem().getValue();
//                            String text = "";
//                            try {
//                                text = String.format("ID: %s\nName: %s\nClass: %s\n", obj.getID().toString(), obj.getName(), obj.getJEVisClass().getName());
//                            } catch (JEVisException ex) {
//                                Logger.getLogger(UnitTree.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                            content.putString(text);
//                            clip.setContent(content);
//                            t.consume();
//                        } else if (add.match(t)) {
//                            System.out.println("New hotkey");
//                            fireEventNew(selectedObject);
//                            t.consume();
//                        }
//
//                    } catch (Exception ex) {
//                        System.out.println("execption while tree key event: " + ex);
//                    }
//                }
//
//            });
            setId("objecttree");//take same css as object tree

            getStylesheets().add("/styles/Styles.css");
            setPrefWidth(500);

            setRoot(rootItem);
            getSelectionModel().select(rootItem);
            setEditable(true);

            //test
//            try {
//                Unit bit = SI.BIT;
//                System.out.println("bit: " + bit);
//                Unit bit2 = bit.alternate("sbit");
//                System.out.println("bit2: " + bit2);
//
//                Unit db = NonSI.DECIBEL;
//                System.out.println("db: " + db);
//                Unit db2 = db.alternate("sdb");
//                System.out.println("sdb: " + db2);
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
        } catch (Exception ex) {
//            Logger.getLogger(ObjectTree.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

    }

    public UnitObject getDragItem() {
        return _dragObj;
    }

    public void setDragItem(UnitObject obj) {
        _dragObj = obj;
    }

    public UnitGraphic getObjectGraphic(UnitObject object) {
        if (_graphicCache.containsKey(object.toString())) {
            return _graphicCache.get(object.toString());
        }

//        System.out.println("grahic does not exist create for: " + object);
        UnitGraphic graph = new UnitGraphic(object, this);
        _graphicCache.put(object.toString(), graph);

        return graph;
    }

    public TreeItem<UnitObject> getObjectTreeItem(UnitObject object) {
        return buildItem(object);
    }

    public TreeItem<UnitObject> buildItem(UnitObject object) {

        if (_itemCache.containsKey(object.toString())) {
            return _itemCache.get(object.toString());
        }

//        System.out.println("buildItem: " + object);
        final TreeItem<UnitObject> newItem = new UnitItem(object, this);
        _itemCache.put(object.toString(), newItem);

        return newItem;
    }

    public void addChildrenList(TreeItem<UnitObject> item, ObservableList<TreeItem<UnitObject>> list) {
//        System.out.println("addChildrenList: " + item);
        _itemChildren.put(item, list);

        if (item.getValue().getType() == UnitObject.Type.FakeRoot) {
            for (Unit child : UnitManager.getInstance().getQuantities()) {
//                System.out.println("---add quanti: " + child);
                UnitObject quant = new UnitObject(UnitObject.Type.Quntity, child, UnitManager.getInstance().getQuantitiesName(child, Locale.ENGLISH));
                TreeItem<UnitObject> newItem = buildItem(quant);
//                TreeItem<UnitObject> newItem = buildItem(child.alternate(UnitManager.getInstance().getQuantitiesName(child, Locale.ENGLISH)));
                list.add(newItem);

            }
        } else if (item.getValue().getType() == UnitObject.Type.Quntity) {
            for (Unit child : UnitManager.getInstance().getCompatibleSIUnit(item.getValue().getUnit())) {
//                System.out.println("------add SI Unit childList: " + child);
                UnitObject quant = new UnitObject(UnitObject.Type.SIUnit, child, item.getValue().getID() + child.toString());
                TreeItem<UnitObject> newItem = buildItem(quant);
                list.add(newItem);
            }

            for (Unit child : UnitManager.getInstance().getCompatibleNonSIUnit(item.getValue().getUnit())) {
//                System.out.println("------add NonSI Unit childList: " + child);
                UnitObject quant = new UnitObject(UnitObject.Type.NonSIUnit, child, item.getValue().getID() + child.toString());
                TreeItem<UnitObject> newItem = buildItem(quant);
                list.add(newItem);
            }

            for (Unit child : UnitManager.getInstance().getCompatibleAdditionalUnit(item.getValue().getUnit())) {
//                System.out.println("------add Additonal Unit childList: " + child);
                UnitObject quant = new UnitObject(UnitObject.Type.NonSIUnit, child, item.getValue().getID() + child.toString());
                TreeItem<UnitObject> newItem = buildItem(quant);
                list.add(newItem);

            }
        } else if (item.getValue().getType() == UnitObject.Type.NonSIUnit || item.getValue().getType() == UnitObject.Type.SIUnit) {
            for (Unit child : getLabels(item.getValue().getUnit())) {
//                System.out.println("----------add labels Unit childList: " + child);
                UnitObject quant = new UnitObject(UnitObject.Type.AltSymbol, child, item.getValue().getID() + child.toString());
                TreeItem<UnitObject> newItem = buildItem(quant);
                list.add(newItem);

            }
        }

        sortList(list);
    }

    //testfake
    public List<Unit> getLabels(Unit unit) {
        List<Unit> list = new ArrayList<>();

        if (unit.equals(SI.WATT.times(NonSI.HOUR))) {
//            list.add(SI.WATT.times(NonSI.HOUR).alternate("var"));
        } else if (unit.equals(NonSI.DECIBEL)) {
            Unit krach = SI.WATT.alternate("Lärm");
            System.out.println("Krach: " + krach);
            list.add(krach);
        }

        return list;
    }

    public ObservableList<TreeItem<UnitObject>> getChildrenList(TreeItem<UnitObject> item) {
        if (item == null || item.getValue() == null) {
            return _emtyList;
        }

        if (_itemChildren.containsKey(item)) {
            return _itemChildren.get(item);
        }
        System.out.println("why");
        ObservableList<TreeItem<UnitObject>> list = FXCollections.observableArrayList();

//        for (Unit child : UnitManager.getInstance().getCompatibleSIUnit(item.getValue().getUnit())) {
//            TreeItem<UnitObject> newItem = buildItem(child);
//            list.add(newItem);
//        }
//        sortList(list);
        _itemChildren.put(item, list);

        return list;

    }

//    private void getAllExpanded(List<TreeItem<Unit>> list, TreeItem<Unit> item) {
//        if (item.isExpanded()) {
//            list.add(item);
//            for (TreeItem<Unit> i : item.getChildren()) {
//                getAllExpanded(list, i);
//            }
//        }
//    }
//
//    private void expandAll(List<TreeItem<Unit>> list, TreeItem<Unit> root) {
////        System.out.println("expand all");
//        for (final TreeItem<Unit> item : root.getChildren()) {
//            for (final TreeItem<Unit> child : list) {
//                if (item.getValue().getID().equals(child.getValue().getID())) {
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            item.setExpanded(true);
//                        }
//                    });
//
//                }
//            }
//            expandAll(list, item);
//        }
//    }
    public void reload() {

    }

    public void expandSelected(boolean expand) {
        TreeItem<UnitObject> item = getSelectionModel().getSelectedItem();
        expand(item, expand);
    }

    private void expand(TreeItem<UnitObject> item, boolean expand) {
        if (!item.isLeaf()) {
            if (item.isExpanded() && !expand) {
                item.setExpanded(expand);
            } else if (!item.isExpanded() && expand) {
                item.setExpanded(expand);
            }

            for (TreeItem<UnitObject> child : item.getChildren()) {
                expand(child, expand);
            }
        }
    }

    public void fireSaveAttributes(boolean ask) throws JEVisException {

//        if (ask) {
//            _editor.checkIfSaved(null);
//        } else {
//            _editor.commitAll();
//        }
    }

    public void fireDelete(Unit obj) {
//        ConfirmDialog dia = new ConfirmDialog();
//        String question = "Do you want to delete the Class \"" + obj.getName() + "\" ?";
//
//        if (dia.show(JEConfig.getStage(), "Delete Object", "Delete Object?", question) == ConfirmDialog.Response.YES) {
//            try {
//                System.out.println("User want to delete: " + obj.getName());
//
//                obj.delete();
//                getObjectTreeItem(obj).getParent().getChildren().remove(getObjectTreeItem(obj));
//                getSelectionModel().select(getObjectTreeItem(obj).getParent());
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
////                    Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);
//
//            }
//        }
    }

    public UnitObject getSelectedObject() {
        return getSelectionModel().getSelectedItem().getValue();
    }

    public void fireEventNew(final Unit parent) {

//        NewObjectDialog dia = new NewObjectDialog();
////        JEVisObject currentObject = _cl.getCurrentItem().getValue().getObject();
////        final TreeItem currentItem = _cl.getCurrentItem();
//
//        if (parent != null) {
//            if (dia.show(JEConfig.getStage(), null, parent, false, NewObjectDialog.Type.NEW, null) == NewObjectDialog.Response.YES) {
//                System.out.println("create new: " + dia.getCreateName() + " class: " + dia.getCreateClass() + " " + dia.getCreateCount() + " times");
//
//                for (int i = 0; i < dia.getCreateCount(); i++) {
//                    try {
//                        //TODo check for uniq
////                if(!dia.getCreateClass().isUnique()){
////                    
////                }
//                        String name = dia.getCreateName();
//                        if (dia.getCreateCount() > 1) {
//                            name += " " + i;
//                        }
//
//                        JEVisObject newObject = parent.buildObject(name, dia.getCreateClass());
//                        newObject.commit();
//                        final TreeItem<JEVisObject> newTreeItem = buildItem(newObject);
//                        TreeItem<JEVisObject> parentItem = getObjectTreeItem(parent);
//
//                        parentItem.getChildren().add(newTreeItem);
//
//                        Platform.runLater(new Runnable() {
//                            @Override
//                            public void run() {
//                                getSelectionModel().select(newTreeItem);
//                            }
//                        });
//
//                    } catch (JEVisException ex) {
//                        //TODO: Cancel all if one faild befor he has to see the exeption dia.getCreateCount() times
//                        Logger.getLogger(UnitTree.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//
//            }
//        }
    }

    //TODO i dont like this way
    public UnitEditor getEditor() {
        return _editor;
    }

    public static void sortList(ObservableList<TreeItem<UnitObject>> list) {
        Comparator<TreeItem<UnitObject>> sort = new Comparator<TreeItem<UnitObject>>() {

            @Override
            public int compare(TreeItem<UnitObject> o1, TreeItem<UnitObject> o2) {
//                System.out.println("Compare: \n " + o1 + " with\n " + o2);
                return o1.getValue().getName().compareTo(o2.getValue().getName());

            }
        };

        FXCollections.sort(list, sort);

    }

    /**
     *
     */
    public class ObjectCell extends TreeCell<UnitObject> {

        @Override
        protected void updateItem(final UnitObject obj, boolean emty) {
            super.updateItem(obj, emty);
            if (!emty) {
                UnitGraphic grph = getObjectGraphic(obj);
                setText(grph.getText());
                setGraphic(grph.getGraphic());
                setTooltip(grph.getToolTip());
//                setContextMenu(grph.getContexMenu());

                //---------------------- Drag & Drop part --------------
//                setOnDragDetected(new EventHandler<MouseEvent>() {
//
//                    @Override
//                    public void handle(MouseEvent e) {
//                        System.out.println("Drag Source: " + obj.toString());
//                        ClipboardContent content = new ClipboardContent();
////                        content.putString(obj.getName());
//                        Dragboard dragBoard = startDragAndDrop(TransferMode.ANY);
//                        content.put(DataFormat.PLAIN_TEXT, obj.toString());
//                        dragBoard.setContent(content);
//
//                        setDragItem(obj);
//                        e.consume();
//                    }
//                });
//
//                setOnDragDone(new EventHandler<DragEvent>() {
//                    @Override
//                    public void handle(DragEvent dragEvent) {
//                        System.out.println("Drag done on " + obj.toString());
//                        dragEvent.consume();
//                    }
//                });
//
//                //TODO: ceh if its ok to move the Object here
//                setOnDragOver(new EventHandler<DragEvent>() {
//                    @Override
//                    public void handle(DragEvent dragEvent) {
//                        System.out.println("Drag Over: " + obj.toString());
//
//                        try {
//                            if (getDragItem().isAllowedUnder(obj)) {
//                                dragEvent.acceptTransferModes(TransferMode.ANY);
//                            }
//
//                            if (obj.getJEVisClass().getName().equals("Views Directory") || obj.getJEVisClass().getName().equals(CommonClasses.LINK.NAME)) {
//                                dragEvent.acceptTransferModes(TransferMode.ANY);
//                            }
//
//                        } catch (JEVisException ex) {
//                            Logger.getLogger(UnitTree.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//
//                        dragEvent.consume();
//
//                    }
//                });
//
//                setOnDragDropped(new EventHandler<DragEvent>() {
//                    @Override
//                    public void handle(final DragEvent dragEvent) {
//                        System.out.println("\nDrag dropped on " + obj.getName());
//                        System.out.println("To Drag: " + getDragItem().getName());
//                        dragEvent.consume();//to disable the drag cursor
//
//                        Platform.runLater(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (dragEvent.isAccepted()) {
////                                    JEConfig.getStage().getScene().setCursor(Cursor.DEFAULT);
//                                    showMoveDialog(_dragObj, obj);
//                                }
//
//                            }
//                        });
//
//                    }
//                });
            }

        }
    }

}
