package edu.usc.csci310.project;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverUtil {
    public static WebDriver initializeDriver() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        String driverPath = "/src/test/resources/chromedriver";

        if (osName.contains("linux")) {
            driverPath = "/src/test/resources/chromedriver_linux";
        } else if (osName.contains("mac") && osArch.contains("aarch64")) {
            driverPath = "/src/test/resources/chromedriver_apple";
        }

        String dir = System.getProperty("user.dir");
        System.setProperty("webdriver.chrome.driver", dir + driverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return new ChromeDriver(options);
    }
}
