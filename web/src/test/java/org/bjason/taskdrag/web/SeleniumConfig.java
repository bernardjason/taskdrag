package org.bjason.taskdrag.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class SeleniumConfig {
    private WebDriver driver;

    public WebDriver getDriver() {
        return driver;
    }

    public SeleniumConfig() {
        FirefoxOptions options = new FirefoxOptions();
        //options.addArguments("--headless");
        driver = new FirefoxDriver(options);

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    static {
        String os = System.getProperty("os.name","linux");
        if ( os.compareToIgnoreCase("linux") == 0 ) {
            System.setProperty("webdriver.gecko.driver", findFile("geckodriver"));
        } else if ( os.startsWith("Windows")  ) {
            System.setProperty("webdriver.gecko.driver", findFile("geckodriver.exe"));
        } else {
            System.out.println("Dont have driver for this os");
            System.exit(-1);
        }
    }

    static private String findFile(String filename) {
        String paths[] = {"", "bin/", "target/classes"};
        for (String path : paths) {
            if (new File(path + filename).exists())
                return path + filename;
        }
        return "";
    }
}
