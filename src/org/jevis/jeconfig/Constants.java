/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public interface Constants {

    public interface Plugin {

        public static String OBJECT = "OBJECT";

        public interface Command {

            public static int SAVE = 0;
            public static int DELTE = 1;
            public static int NEW = 2;
            public static int COPY = 3;
            public static int PASE = 4;
            public static int EXPAND = 5;
            public static int COLLAPSE = 6;
        }
    }

    public interface Color {

        public static String MID_BLUE = "#005782";
        public static String MID_GREY = "#666666";
        public static String LIGHT_BLUE = "#1a719c";
        public static String LIGHT_BLUE2 = "#0E8CCC";
        public static String LIGHT_GREY = "#efefef";
    }
}
