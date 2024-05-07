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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.*;
import java.time.Duration;

import static edu.usc.csci310.project.WebDriverUtil.initializeDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SearchStepDefs {

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
                ps.setString(1, "testuser3@example.com");
                ps.executeUpdate();
            }
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
        } catch (SQLException e) {
            System.out.println("Could not purge users in search step definitions:\n" + e.getMessage());
        }
    }

    @Before
    public void setUp() throws InterruptedException {
        driver = initializeDriver();

        driver.get("http://localhost:8080/signup");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name"))).sendKeys("Test User");
        driver.findElement(By.id("email")).sendKeys("testuser3@example.com");
        driver.findElement(By.id("password")).sendKeys("testPassword123");
        driver.findElement(By.id("passwordConfirmation")).sendKeys("testPassword123");

        driver.findElement(By.cssSelector("input[type='submit'][value='Sign Up']")).click();

        Thread.sleep(1000);
    }

    @Given("I am on the \"Search\" page")
    public void givenOnSearchPage() throws InterruptedException {

        driver.get("http://localhost:8080/search");

        // allow all dropdowns to load
        Thread.sleep(3000);
    }

    @When("The user enters {string} into the searchbar")
    public void userEntersSearch(String search) {
        WebElement searchField = driver.findElement(By.id("search-field"));
        searchField.clear();
        searchField.sendKeys(search);
    }

    @When("User clicks the search button")
    public void userClicksSearchButton() throws InterruptedException {
        WebElement searchButton = driver.findElement(By.id("search-button"));
        searchButton.click();

        Thread.sleep(1000);
    }

    @And("I see {string}")
    public void iSeeResults(String expectedResults) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement resultsElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("results-length")));

        String actualResults = resultsElement.getText();
        assertEquals(expectedResults, actualResults, "The results text does not match expected.");
    }

    @Then("I should see {string}")
    public void iShouldSeeResults(String expectedResults) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement resultsElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("results-length")));

        String actualResults = resultsElement.getText();
        assertEquals(expectedResults, actualResults, "The results text does not match expected.");
    }

    @Then("I should see {string} message")
    public void iShouldSeeNonexistentParkMessage(String expectedResults) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement resultsElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("no-results-message")));

        String actualResults = resultsElement.getText();
        assertEquals(expectedResults, actualResults, "The results text does not match expected.");
    }

    @And("The user filters by activity to limit results to parks with {string}")
    public void theUserFiltersByActivityToLimitResultsToParksWith(String activity) {
        Select dropdown = new Select(driver.findElement(By.id("activity")));
        dropdown.selectByVisibleText(activity);
    }

    @And("The user filters by amenity to limit results to parks with {string}")
    public void theUserFiltersByAmenityToLimitResultsToParksWith(String amenity) {
        Select dropdown = new Select(driver.findElement(By.id("amenity")));
        dropdown.selectByVisibleText(amenity);
    }

    @And("The user filters by state to limit results to parks in {string}")
    public void theUserFiltersByStateToLimitResultsToParksIn(String state) {
        Select dropdown = new Select(driver.findElement(By.id("state")));
        dropdown.selectByVisibleText(state);
    }

    @After
    public void tearDown() {
        purgeUser();

        if (driver != null) {
            driver.quit();
        }
    }
}
