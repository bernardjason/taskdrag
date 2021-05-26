package org.bjason.taskdrag.javafx;

import javafx.stage.Stage;

public interface MainAppInterface {
    public void doLogin(String userName, String passWord) throws Exception ;

    void loggedIn(Stage primaryStage) throws Exception;
}
