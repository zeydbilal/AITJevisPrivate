/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig;

import javafx.scene.image.ImageView;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public interface Command {

    String getCode();

    public class Save implements Command {

        @Override
        public String getCode() {
            return "213213213";
        }
    }
}
