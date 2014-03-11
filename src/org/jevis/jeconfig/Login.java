/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig;

import javafx.geometry.Insets;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Dialogs.DialogOptions;
import javafx.scene.control.Dialogs.DialogResponse;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import org.jevis.jeapi.JEVisDataSource;
import org.jevis.jeapi.JEVisException;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class Login {

    public String usernameResult = null;
    public String passwordResult = null;
    public String serverResult = null;
    public JEVisDataSource _ds;

    public Login(JEVisDataSource ds, String user, String pw, String server, String langue) {
        _ds = ds;
    }

    public Login(JEVisDataSource _ds) {
        this._ds = _ds;
    }

    public void showLogin(boolean wasWrong) throws JEVisException {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));
        final TextField username = new TextField();
        username.setPromptText("Username");
        final PasswordField password = new PasswordField();
        password.setPromptText("Password");
//        final TextField server = new TextField();
//        username.setPromptText("Server");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        Label pww = new Label("Wrong User/Password");
        if (wasWrong) {
            pww.setStyle("-fx-text-fill: Color.rgb(210, 39, 30);");
        } else {
            pww.setStyle("-fx-text-fill: rgba(0, 100, 100, 0);");
        }

        grid.add(pww, 0, 2);



        Callback<Void, Void> myCallback = new Callback<Void, Void>() {
            @Override
            public Void call(Void param) {
                usernameResult = username.getText();
                passwordResult = password.getText();
//                serverResult = server.getText();
                return null;
            }
        };

        DialogResponse resp = Dialogs.showCustomDialog(JEConfig.getStage(), grid, "Please log in", "Login", DialogOptions.OK_CANCEL, myCallback);
        if (resp == DialogResponse.OK) {
            try {
                _ds.connect(usernameResult, passwordResult);
            } catch (Exception ex) {
                showLogin(true);
            }
        } else {
            System.exit(0);
        }


    }
}
