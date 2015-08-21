/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.sample;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.jevis.api.JEVisOption;

/**
 *
 * @author Florian Simon
 */
public class OptionTreeItem extends TreeItem<JEVisOption> {

    private boolean isLeaf = true;

    public OptionTreeItem(JEVisOption value) {
        super(value);

        isLeaf = value.getChildren().isEmpty();

        for (JEVisOption opt : value.getChildren()) {
//            System.out.println("--addChild: " + opt.getKey());
            OptionTreeItem item = new OptionTreeItem(opt);
            item.setExpanded(true);
            super.getChildren().add(item);
        }

        super.getChildren().addListener(new ListChangeListener<TreeItem<JEVisOption>>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends TreeItem<JEVisOption>> c) {
                if (!getChildren().isEmpty()) {
                    isLeaf = false;
                }
            }
        });

    }

//    @Override
//    public ObservableList<TreeItem<JEVisOption>> getChildren() {
//        System.out.println("-");
//
//
//        return super.getChildren(); //To change body of generated methods, choose Tools | Templates.
//    }
    @Override
    public String toString() {
        return getValue().getKey();
    }

    @Override
    public boolean isLeaf() {
        return isLeaf;

//        return getValue().getChildren().isEmpty();
    }

}
