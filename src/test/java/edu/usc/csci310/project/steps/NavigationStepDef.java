package edu.usc.csci310.project.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.*;
import java.time.Duration;

import static edu.usc.csci310.project.WebDriverUtil.initializeDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NavigationStepDef {
    private WebDriver driver;

    public static void purgeUser() {
        String osName = System.getProperty("os.name").toLowerCase();
        String dbUrl = "jdbc:mysql://localhost:3307/testDB?user=root&password=1q2w3e4r!@";
        if (osName.contains("linux")) {
            dbUrl = "jdbc:mysql://db:3306/testDB?user=root&password=1q2w3e4r!@";
        }
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            Statement stmt = conn.createStatement();
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            String sql = "DELETE FROM user WHERE email = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "testuser6@example.com");
                ps.executeUpdate();
            }
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {
        driver = initializeDriver();

        driver.get("http://localhost:8080/signup");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name"))).sendKeys("Test User");
        driver.findElement(By.id("email")).sendKeys("testuser6@example.com");
        driver.findElement(By.id("password")).sendKeys("testPassword123");
        driver.findElement(By.id("passwordConfirmation")).sendKeys("testPassword123");

        driver.findElement(By.cssSelector("input[type='submit'][value='Sign Up']")).click();

        try {
            Thread.sleep(1_000);
        } catch (Exception e) {}
    }

    @Given("I am on the home page")
    public void iAmOnTheHomePage() throws InterruptedException {
        driver.get("http://localhost:8080/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#homepage-image")));

        Thread.sleep(1_000);
    }

    @When("I click the {string} button in the navigation bar")
    public void iClickTheButtonInTheNavigationBar(String navText) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.linkText(navText)));
        button.click();
    }

    @And("I am taken to the search page")
    public void iAmTakenToTheSearchPage() {
        String expectedUrl = "http://localhost:8080/search";
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.urlToBe(expectedUrl));
        assertEquals(expectedUrl, driver.getCurrentUrl());
    }

    @And("I log out successfully")
    public void iLogOutSuccessfully() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Logout")));
        button.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#homepage-image")));
    }

    @Then("I should be taken to the home page")
    public void iShouldBeTakenToTheHomePage() {
        String expectedUrl = "http://localhost:8080/";
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.urlToBe(expectedUrl));
        assertEquals(expectedUrl, driver.getCurrentUrl());
    }

    @Then("I should be taken to the {string} page")
    public void iShouldBeTakenToThePage(String pageUrl) {
        String expectedUrl = "http://localhost:8080/" + pageUrl;
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.urlToBe(expectedUrl));
        assertEquals(expectedUrl, driver.getCurrentUrl());
    }

    @After
    public void tearDown() {
        purgeUser();

        if (driver != null) {
            driver.quit();
        }
    }
}
