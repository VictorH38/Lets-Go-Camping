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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParkDetailsStepDef {
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
                ps.setString(1, "testuser2@example.com");
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
        driver.findElement(By.id("email")).sendKeys("testuser2@example.com");
        driver.findElement(By.id("password")).sendKeys("testPassword123");
        driver.findElement(By.id("passwordConfirmation")).sendKeys("testPassword123");

        driver.findElement(By.cssSelector("input[type='submit'][value='Sign Up']")).click();

        try {
            Thread.sleep(1_000);
        } catch (Exception e) {}
    }

    @Given("I am on the park search page")
    public void i_am_on_the_park_search_page() {
        driver.get("http://localhost:8080/search");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#search-field")));
    }

    @And("I search for {string}")
    public void i_search_for(String parkName) {
        WebElement searchField = driver.findElement(By.id("search-field"));
        searchField.sendKeys(parkName);
    }

    @And("I click on the search button")
    public void iClickOnTheSearchButton() {
        WebElement searchButton = driver.findElement(By.id("search-button"));
        searchButton.click();
    }

    @Then("I should see a title that says {string}")
    public void i_should_see_a_title_that_says(String expectedTitle) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement parkTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("park-title-193")));
        assertEquals(expectedTitle, parkTitle.getText(), "The park title does not match the expected value.");
    }

    @When("I click on the {string} card")
    public void i_click_on_the_card(String parkName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement parkCard = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[text()='" + parkName + "']/ancestor::div[contains(@class,'park-card')]")));
        parkCard.click();
    }

    @Then("I should see the expanded details of the park")
    public void i_should_see_the_expanded_details_of_the_park() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement addressElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search-address-193")));
        assertTrue(addressElement.isDisplayed(), "Address details are not displayed");
        String address = addressElement.getText();
        assertTrue(address.contains("20 South Entrance Road, AZ 86023"), "Address text is incorrect");

        WebElement latitudeElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search-latitude-193")));
        assertTrue(latitudeElement.isDisplayed(), "Latitude details are not displayed");
        String latitude = latitudeElement.getText();
        assertTrue(latitude.contains("36.0001165336"), "Latitude text is incorrect");

        WebElement longitudeElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search-longitude-193")));
        assertTrue(longitudeElement.isDisplayed(), "Longitude details are not displayed");
        String longitude = longitudeElement.getText();
        assertTrue(longitude.contains("-112.121516363"), "Longitude text is incorrect");

        WebElement weatherElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search-weather-193")));
        assertTrue(weatherElement.isDisplayed(), "Weather details are not displayed");
        String weather = weatherElement.getText();
        assertTrue(weather.contains("This weather varies with cold winters and mild pleasant summers, moderate humidity, and considerable diurnal temperature changes at the higher elevations, with hot and drier summers at the bottom of the Grand Canyon along with cool damp winters. Summer thunderstorms and winter snowfall adds to the weather variety in this region."), "Weather text is incorrect");
    }

    @Then("I should see a message indicating {string}")
    public void i_should_see_a_message_indicating(String expectedMessage) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement noResultsMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h4[contains(normalize-space(.), 'No results for \"Nonexistent Park\"')]")));

        assertTrue(noResultsMessage.isDisplayed(), "Expected message is not displayed.");
        assertEquals(expectedMessage, noResultsMessage.getText().trim(), "The displayed message does not match the expected text.");
    }

    @And("I click on the favorites button")
    public void iClickOnTheFavoritesButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement favoritesButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("search-favorites-button-193")));

        favoritesButton.click();
    }

    @And("I click on the favorites button again")
    public void iClickOnTheFavoritesButtonAgain() throws InterruptedException {
        Thread.sleep(1_000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement favoritesButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("search-favorites-button-193")));

        favoritesButton.click();
    }

    @Then("the favorites button should change to {string}")
    public void theFavoritesButtonShouldChangeTo(String expectedText) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement favoritesButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("search-favorites-button-193")));

        wait.until(ExpectedConditions.textToBePresentInElement(favoritesButton, expectedText));
        assertEquals(expectedText, favoritesButton.getText());
    }

    @And("I see the remove from favorites confirmation")
    public void iSeeTheRemoveFromFavoritesConfirmation() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement confirmationDialog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("confirmation-dialog")));

        WebElement confirmationText = confirmationDialog.findElement(By.className("remove-confirmation-text"));
        assertEquals("Are you sure you want to remove this park from favorites?", confirmationText.getText());
    }

    @And("I click yes")
    public void iClickYes() {
        WebElement yesButton = driver.findElement(By.className("remove-yes"));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(yesButton));

        yesButton.click();
    }

    @And("I click no")
    public void iClickNo() {
        WebElement yesButton = driver.findElement(By.className("remove-no"));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(yesButton));

        yesButton.click();
    }

    @Then("the favorites button should still say {string}")
    public void theFavoritesButtonShouldStillSay(String expectedText) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement favoritesButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("search-favorites-button-193")));

        wait.until(ExpectedConditions.textToBePresentInElement(favoritesButton, expectedText));
        assertEquals(expectedText, favoritesButton.getText());
    }

    @After
    public void tearDown() {
        purgeUser();

        if (driver != null) {
            driver.quit();
        }
    }
}
