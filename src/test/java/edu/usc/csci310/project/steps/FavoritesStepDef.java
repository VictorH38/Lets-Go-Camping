package edu.usc.csci310.project.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.*;
import java.time.Duration;

import static edu.usc.csci310.project.WebDriverUtil.initializeDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FavoritesStepDef {
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
                ps.setString(1, "testuser4@example.com");
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
        driver.findElement(By.id("email")).sendKeys("testuser4@example.com");
        driver.findElement(By.id("password")).sendKeys("testPassword123");
        driver.findElement(By.id("passwordConfirmation")).sendKeys("testPassword123");

        driver.findElement(By.cssSelector("input[type='submit'][value='Sign Up']")).click();

        try {
            Thread.sleep(1_000);
        } catch (Exception e) {}
    }

    @Given("I am on the favorites page")
    public void iAmOnTheFavoritesPage() {
        driver.get("http://localhost:8080/favorites");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#favorites-title")));
    }

    @And("I haven't added any parks to favorite")
    public void iHavenTAddedAnyParksToFavorite() {
    }

    @Then("I should see {string} on the favorites page")
    public void iShouldSeeOnTheFavoritesPage(String expectedMessage) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Thread.sleep(1000);
        WebElement favoritesTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("favorites-title")));

        String actualMessage = favoritesTitle.getText();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Given("I have added Grand Canyon National Park to my favorites list")
    public void iHaveAddedGrandCanyonNationalParkToMyFavoritesList() {
        driver.get("http://localhost:8080/search");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#search-field")));

        WebElement searchField = driver.findElement(By.id("search-field"));
        searchField.sendKeys("Grand Canyon National Park");

        WebElement searchButton = driver.findElement(By.id("search-button"));
        searchButton.click();

        WebElement parkTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("park-title-193")));
        assertEquals("Grand Canyon National Park", parkTitle.getText(), "The park title does not match the expected value.");

        WebElement favoritesButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("search-favorites-button-193")));
        favoritesButton.click();

        wait.until(ExpectedConditions.textToBePresentInElement(favoritesButton, "Remove from Favorites"));
        assertEquals("Remove from Favorites", favoritesButton.getText());
    }

    @And("I have added Yosemite National Park to my favorites list")
    public void iHaveAddedYosemiteNationalParkToMyFavoritesList() {
        driver.get("http://localhost:8080/search");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#search-field")));

        WebElement searchField = driver.findElement(By.id("search-field"));
        searchField.sendKeys("Yosemite National Park");

        WebElement searchButton = driver.findElement(By.id("search-button"));
        searchButton.click();

        WebElement parkTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("park-title-468")));
        assertEquals("Yosemite National Park", parkTitle.getText(), "The park title does not match the expected value.");

        WebElement favoritesButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("search-favorites-button-468")));
        favoritesButton.click();

        wait.until(ExpectedConditions.textToBePresentInElement(favoritesButton, "Remove from Favorites"));
        assertEquals("Remove from Favorites", favoritesButton.getText());
    }

    @And("I go to the favorites page")
    public void iGoToTheFavoritesPage() {
        driver.get("http://localhost:8080/favorites");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#favorites-title")));
    }

    @Then("I should see the {string} card on the favorites page")
    public void iShouldSeeTheCardOnTheFavoritesPage(String expectedTitle) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement parkTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("favorite-park-title-193")));
        assertEquals(expectedTitle, parkTitle.getText(), "The park title does not match the expected value.");
    }

    @And("I remove {string} from my favorites")
    public void iRemoveFromMyFavorites(String arg0) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement favoritesButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("favorites-button-193")));
        favoritesButton.click();

        WebElement yesButton = driver.findElement(By.className("remove-yes"));
        wait.until(ExpectedConditions.elementToBeClickable(yesButton));
        yesButton.click();
    }

    @And("Grand Canyon National Park is above Yosemite National Park on my favorites list")
    public void grandCanyonNationalParkIsAboveYosemiteNationalParkOnMyFavoritesList() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#favorites-title")));

        WebElement grandCanyon = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='droppable-193']")));
        WebElement yosemite = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='droppable-468']")));

        int yPositionGrandCanyon = grandCanyon.getLocation().getY();
        int yPositionYosemite = yosemite.getLocation().getY();

        assertTrue(yPositionGrandCanyon < yPositionYosemite);
    }

    @And("I drag Grand Canyon National Park below Yosemite National Park on my favorites list")
    public void iDragGrandCanyonNationalParkBelowYosemiteNationalParkOnMyFavoritesList() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        Actions action = new Actions(driver);
        for (int i = 0; i < 8; i++) {
            action.sendKeys(Keys.TAB).perform();
        }

        WebElement activeElement = driver.switchTo().activeElement();
        wait.until(ExpectedConditions.attributeContains(activeElement, "data-testid", "droppable-193"));

        action.sendKeys(Keys.SPACE).perform();
        action.sendKeys(Keys.ARROW_DOWN).perform();
        action.sendKeys(Keys.SPACE).perform();
    }

    @Then("I should see Grand Canyon National Park below Yosemite National Park on my favorites list")
    public void iShouldSeeGrandCanyonNationalParkBelowYosemiteNationalParkOnMyFavoritesList() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#favorites-title")));

        WebElement grandCanyon = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='droppable-193']")));
        WebElement yosemite = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='droppable-468']")));

        int yPositionGrandCanyon = grandCanyon.getLocation().getY();
        int yPositionYosemite = yosemite.getLocation().getY();

        assertTrue(yPositionGrandCanyon > yPositionYosemite);
    }

    @And("the publicity button says {string}")
    public void thePublicityButtonSays(String expectedLabel) throws InterruptedException {
        Thread.sleep(1000);
        WebElement publicityButton = driver.findElement(By.cssSelector("[data-testid='publicity-button']"));
        String actualLabel = publicityButton.getText();
        assertEquals(expectedLabel, actualLabel);
    }

    @When("I click on the publicity button")
    public void iClickOnThePublicityButton() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#favorites-title")));

        WebElement publicityButton = driver.findElement(By.cssSelector("[data-testid='publicity-button']"));
        publicityButton.click();
        Thread.sleep(1000);
    }

    @Then("the publicity button should change to {string}")
    public void thePublicityButtonShouldChangeTo(String expectedLabel) {
        WebElement publicityButton = driver.findElement(By.cssSelector("[data-testid='publicity-button']"));
        String actualLabel = publicityButton.getText();
        assertEquals(expectedLabel, actualLabel);
    }

    @After
    public void tearDown() {
        purgeUser();

        if (driver != null) {
            driver.quit();
        }
    }
}
