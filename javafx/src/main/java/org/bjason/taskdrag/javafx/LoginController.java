package org.bjason.taskdrag.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @FXML
    public TextField username;
    @FXML
    public TextField password;
    @FXML
    public Button login;
    @FXML
    public TextArea errors;
    public Label loginFeedbackId;
    private Stage primaryStage;
    private MainAppInterface main;


    public void loginAction(ActionEvent actionEvent) {
        String userName = username.getText();
        String passWord = password.getText();

        if ( userName == null || userName.length() == 0 || passWord == null || passWord.length() == 0 ) {
            errors.setWrapText(true);
            errors.setText("Username and password must be entered");
        }  else {
            try {
                main.doLogin(userName,passWord);
                main.loggedIn(primaryStage);
            } catch (Exception e) {
                errors.setWrapText(true);
                loginFeedbackId.setText("Login failed on backend");
                errors.setText(e.getMessage());
                log.info("Error logging in "+e.getMessage());
                log.debug("Error logging in "+e.getMessage(),e);
            }
        }

    }

    public void setMain(MainAppInterface main, Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.main = main;
    }
    static LoginController displayLogin(Stage primaryStage, MainAppInterface main) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = LoginController.class.getResource("/login.fxml");
        loader.setLocation(xmlUrl);

        Parent root = loader.load();

        LoginController msc = loader.getController();
        msc.setMain(main,primaryStage);
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(400);
        primaryStage.setHeight(300);
        primaryStage.show();
        return msc;

    }

    public void close() {
        primaryStage.close();
    }
}
