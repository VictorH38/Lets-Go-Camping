package edu.usc.csci310.project.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.sql.*;

import static edu.usc.csci310.project.WebDriverUtil.initializeDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginStepDefs {

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
                ps.setString(1, "testuser11@example.com");
                ps.executeUpdate();
            }
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
        } catch (SQLException e) {
            System.out.println("Could not purge users in search step definitions:\n" + e.getMessage());
        }
    }

    @Before
    public void setUp() {

        // initialize driver
        driver = initializeDriver();

        // create test user
        driver.get("http://localhost:8080/signup");
        driver.findElement(By.id("name")).sendKeys("test name");
        driver.findElement(By.id("email")).sendKeys("testuser11@example.com");
        driver.findElement(By.id("password")).sendKeys("password123");
        driver.findElement(By.id("passwordConfirmation")).sendKeys("password123");
        driver.findElement(By.cssSelector("input[type='submit']")).click();
    }

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        driver.get("http://localhost:8080/login");
    }
    @When("I enter valid credentials")
    public void iEnterValidCredentials() {
        driver.findElement(By.id("email")).sendKeys("testuser11@example.com");
        driver.findElement(By.id("password")).sendKeys("password123");
    }

    @When("I enter invalid credentials")
    public void iEnterInvalidCredentials() {
        driver.findElement(By.id("email")).sendKeys("testuser11@example.com");
        driver.findElement(By.id("password")).sendKeys("badpassword123");
    }

    @And("I press the login button")
    public void iPressTheLoginButton() throws InterruptedException {
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("input[type='submit']")).click();
    }

    @Then("I should get logged in")
    public void iShouldGetLoggedIn() throws InterruptedException {
        Thread.sleep(2000);
        assertEquals("http://localhost:8080/", driver.getCurrentUrl());
    }

    @Then("I wait a minute before the next attempt")
    public void iWaitBeforeNextAttempt() throws InterruptedException {
        Thread.sleep(60000);
    }

    @Then("I should get a {string} message on the login page")
    public void iShouldGetAMessageOnTheLoginPage(String arg0) throws InterruptedException {
        Thread.sleep(1000);
        assertEquals(arg0, driver.findElement(By.id("error")).getText());
    }

    @After
    public void tearDown() {
        purgeUser();

        if (driver != null) {
            driver.quit();
        }
    }
}
