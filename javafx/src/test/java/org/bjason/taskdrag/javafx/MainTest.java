package org.bjason.taskdrag.javafx;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import org.bjason.taskdrag.common.CallBackend;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.service.query.NodeQuery;

import java.util.Random;

import static org.bjason.taskdrag.javafx.BackendRunner.GOOD_USER;
import static org.bjason.taskdrag.javafx.BackendRunner.PASSWORD;

@SpringBootTest(classes = {Main.class, SpringMainBean.class, org.springframework.web.client.RestTemplate.class,org.bjason.taskdrag.common.CallBackend.class})
//@ExtendWith(ApplicationExtension.class)
@ExtendWith(Hook.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainTest {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public CallBackend getCallBackEnd() {
        return  new CallBackend();
    }

    static Main main;

    Stage stage;

    @Start
    void start(Stage stage) throws Exception {
        this.stage = stage;
        main = new Main();
        main.isLoggedIn = true;
        main.start(stage);
        for (int i = 0; i < 10 && main.smb == null; i++) {
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
    void testCreateNewWork(FxRobot robot) throws Exception {
        main.doLogin(GOOD_USER,PASSWORD);
        Platform.runLater( () -> {
            try {
                main.startTheMainScene(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Thread.sleep(3000);
        Random r = new Random();
        String textToAdd = "automated test1 random = " + r.nextInt();
        robot.clickOn("#newWork");
        robot.clickOn("#titleFieldId");
        robot.write(textToAdd);
        robot.clickOn("#okButtonId");
        robot.clickOn("#playButtonId");
        FxAssert.verifyThat("#titleFieldId", TextInputControlMatchers.hasText(textToAdd));

        Thread.sleep(1500);
        robot.doubleClickOn("#freetextListViewId");
        robot.clickOn("#freetextFieldId");
        robot.write(textToAdd);
        robot.clickOn("#okButtonId");

        Thread.sleep(1500);

        ListView lv =(ListView) ((java.util.LinkedHashSet) robot.lookup("#freetextListViewId").queryAll()).toArray()[0] ;
        Label l = (Label) lv.getItems().get(0);

        Assert.assertEquals(true,l.getText().contains(textToAdd));
        Assert.assertEquals(main.smb.tableData.get(0).getFreeText().get(0).getText(),textToAdd);

        dragTo("inprogress",robot);
        Assert.assertTrue(robot.lookup("#workTitleBar").queryLabeled().getText().contains("inprogress"));

        robot.drag("#workTitleBar").dropTo("#binImage");
        Thread.sleep(1000);
        robot.rightClickOn("#binImage");
        Thread.sleep(1000);
        Node menu = robot.lookup("#binContextMenu").query() ;
        boolean workHasBeenDeleted = false;
        for(MenuItem x : main.msc.binContextMenu.getItems()) {
            if ( x.getText().contains(":automated test1")) {
                workHasBeenDeleted=true;
            }
        }
        Assert.assertEquals(true,workHasBeenDeleted);
    }

    private void dragTo(String to,FxRobot robot) throws InterruptedException {
        NodeQuery dropHere = robot.lookup("#"+to);
        for(Node n : dropHere.queryAll()) {
            if ( n instanceof ScrollPane) {
                robot.drag("#workTitleBar").dropTo(n);
                Thread.sleep(1000);

            }
        }
    }
}
