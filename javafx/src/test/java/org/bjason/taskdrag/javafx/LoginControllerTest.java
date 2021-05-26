package org.bjason.taskdrag.javafx;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.api.FxRobotInterface;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import static org.bjason.taskdrag.javafx.BackendRunner.GOOD_USER;
import static org.bjason.taskdrag.javafx.BackendRunner.PASSWORD;

@ExtendWith(Hook.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginControllerTest  {

    static Main main;

    Stage stage;

    @Start
    private void start(Stage stage) throws Exception {
        this.stage = stage;
        main = new Main();
        main.start(stage);
        for(int i = 0 ; i < 10  && main.smb == null ; i++ ) {
            Thread.sleep(1000);
            System.out.println("Wait for javafx spring to start");
        }
    }


    @AfterAll
    static void afterAll() {
        Platform.runLater(() -> {
            main.close();
            main = null;
        });
        try {
            while( main != null ) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void loginIntoSystemOk(FxRobot robot) throws InterruptedException {
        robot.clickOn("#username");
        robot.write(GOOD_USER);
        robot.clickOn("#password");
        robot.write(PASSWORD);
        Thread.sleep(1000);
        robot.clickOn("login");
        for (int i = 0; i < 10 && main.isLoggedIn == false; i++) {
            Thread.sleep(500);
            System.out.println("Wait for login.....");
        }
        Assert.assertEquals(true, main.isLoggedIn);

    }

    @Test
    void loginZFails(FxRobotInterface robot) throws Exception {

        // make sure nothing running
        afterAll();
        Platform.runLater(() -> {
            try {
                start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Thread.sleep(1000);
        robot.clickOn("#username");
        robot.write("bernard");
        robot.clickOn("#password");
        robot.write("batman");
        Thread.sleep(1000);
        robot.clickOn("login");
        Thread.sleep(2000);

        FxAssert.verifyThat("#loginFeedbackId", LabeledMatchers.hasText("Login failed on backend"));

    }

}