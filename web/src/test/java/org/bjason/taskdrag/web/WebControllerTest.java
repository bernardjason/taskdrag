package org.bjason.taskdrag.web;

import org.bjason.taskdrag.common.CallBackend;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Date;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebControllerTest {
    private SeleniumConfig config;

    @LocalServerPort
    private int port;

    @Autowired
    CallBackend callBackend;

    @AfterEach
    public void closeWindow() {
        this.config.getDriver().close();
    }

    @BeforeEach
    public void setupSelenium() {
        String url = "http://localhost:"+port+"/";
        callBackend.setBackend("http://localhost:"+port);
        config = new SeleniumConfig();
        config.getDriver().get(url);

    }

    @Test
    public void aSimpleLoginSuccess() throws InterruptedException {
        Actions actions = new Actions(config.getDriver());
        WebElement username = this.config.getDriver().findElement(By.id("username"));
        WebElement password = this.config.getDriver().findElement(By.id("password"));
        WebElement submit = this.config.getDriver().findElement(By.id("loginButton"));

        actions.moveToElement(username).perform();
        actions.click();
        actions.sendKeys("bernard");
        actions.moveToElement(password).perform();
        actions.click();
        actions.sendKeys("jason");
        actions.moveToElement(submit).perform();
        actions.click();
        actions.perform();

        Thread.sleep(1000);

        String actualTitle = config.getDriver().getTitle();

        assertNotNull(actualTitle);
        assertEquals("task drag", actualTitle);
    }

    WebElement findById(String id ) {
        return config.getDriver().findElement(By.id(id));
    }

    @Test
    public void createWork() throws InterruptedException {

        Actions actions = new Actions(config.getDriver());
        actions.moveToElement(findById("username")).perform();
        actions.click();
        actions.sendKeys("bernard");
        actions.moveToElement(findById("password")).perform();
        actions.click();
        actions.sendKeys("jason");
        actions.moveToElement(findById("loginButton")).perform();
        actions.click();
        actions.perform();

        Thread.sleep(500);

        String actualTitle = config.getDriver().getTitle();

        //assertNotNull(actualTitle);
        //assertEquals("task drag", actualTitle);

        actions.moveToElement(findById("title"));
        actions.click();
        Random r = new Random();
        double random = r.nextDouble();
        String work ="test work "+new Date()+" random="+ random;
        actions.sendKeys(work);
        actions.moveToElement(findById("newWorkButton"));
        actions.click();
        actions.perform();

        Thread.sleep(1000);

        WebElement justCreated = config.getDriver().findElement(By.xpath("//div[@id='created']//*[contains(text(),'random="+random+"')]"));
        Thread.sleep(500);
        justCreated.click();
        Thread.sleep(1000);
        WebElement modalOpen = findById("updateWorkModal");
        String style = modalOpen.getAttribute("style");
        assertEquals("display: block;",style);

        findById("closeUpdateCross").click();
        actions.perform();
        Thread.sleep(500);


        ((JavascriptExecutor)config.getDriver()).executeScript("alert('drag by hand to done');");

        Thread.sleep(10000);

        WebElement isDone = config.getDriver().findElement(By.xpath("//div[@id='done']//*[contains(text(),'random="+random+"')]"));

        assertNotNull(isDone);

    }

}