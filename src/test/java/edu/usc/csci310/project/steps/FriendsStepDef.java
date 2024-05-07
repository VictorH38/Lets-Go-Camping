package edu.usc.csci310.project.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.*;
import java.time.Duration;
import java.util.List;

import static edu.usc.csci310.project.WebDriverUtil.initializeDriver;
import static org.junit.jupiter.api.Assertions.*;

public class FriendsStepDef {
    private WebDriver driver;

    public static void purgeUsers() {
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
                ps.setString(1, "testuser5@example.com");
                ps.executeUpdate();
            }
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");

            Statement stmt2 = conn.createStatement();
            stmt2.execute("SET FOREIGN_KEY_CHECKS = 0;");
            String sql2 = "DELETE FROM user WHERE email = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                ps2.setString(1, "johndoe@example.com");
                ps2.executeUpdate();
            }
            stmt2.execute("SET FOREIGN_KEY_CHECKS = 1;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void purgeAllOtherUsers() {
        String osName = System.getProperty("os.name").toLowerCase();
        String dbUrl = "jdbc:mysql://localhost:3307/testDB?user=root&password=1q2w3e4r!@";
        if (osName.contains("linux")) {
            dbUrl = "jdbc:mysql://db:3306/testDB?user=root&password=1q2w3e4r!@";
        }
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            Statement stmt = conn.createStatement();
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            String sql = "DELETE FROM user WHERE email <> ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "testuser5@example.com");
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
        driver.findElement(By.id("email")).sendKeys("testuser5@example.com");
        driver.findElement(By.id("password")).sendKeys("testPassword123");
        driver.findElement(By.id("passwordConfirmation")).sendKeys("testPassword123");

        driver.findElement(By.cssSelector("input[type='submit'][value='Sign Up']")).click();

        try {
            Thread.sleep(1_000);
        } catch (Exception e) {}
    }

    @Given("no other users have signed up")
    public void noOtherUsersHaveSignedUp() {
        purgeAllOtherUsers();
    }

    @Given("another user signs up with the name {string}")
    public void anotherUserSignsUpWithTheName(String name) {
        purgeAllOtherUsers();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Logout")));
        logoutLink.click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/"));
        driver.get("http://localhost:8080/signup");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name"))).sendKeys(name);
        driver.findElement(By.id("email")).sendKeys("johndoe@example.com");
        driver.findElement(By.id("password")).sendKeys("testPassword123");
        driver.findElement(By.id("passwordConfirmation")).sendKeys("testPassword123");

        driver.findElement(By.cssSelector("input[type='submit'][value='Sign Up']")).click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/"));
    }

    @And("John Doe adds Grand Canyon National Park to their favorites")
    public void johnDoeAddsGrandCanyonNationalParkToTheirFavorites() {
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

    @And("I add Grand Canyon National Park to my favorites")
    public void iAddGrandCanyonNationalParkToMyFavorites() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Logout")));
        logoutLink.click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/"));
        driver.get("http://localhost:8080/login");

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("input[type='submit']"));

        emailField.clear();
        emailField.sendKeys("testuser5@example.com");
        passwordField.clear();
        passwordField.sendKeys("testPassword123");
        loginButton.click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/"));
        driver.get("http://localhost:8080/search");
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

    @And("I add Yosemite National Park to my favorites")
    public void iAddYosemiteNationalParkToMyFavorites() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Logout")));
        logoutLink.click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/"));
        driver.get("http://localhost:8080/login");

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("input[type='submit']"));

        emailField.clear();
        emailField.sendKeys("testuser5@example.com");
        passwordField.clear();
        passwordField.sendKeys("testPassword123");
        loginButton.click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/"));
        driver.get("http://localhost:8080/search");
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

    @And("John Doe toggles their profile publicity to private")
    public void johnDoeTogglesTheirProfilePublicityToPrivate() throws InterruptedException {
        driver.get("http://localhost:8080/favorites");

        Thread.sleep(1000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#favorites-title")));

        WebElement publicityButton = driver.findElement(By.cssSelector("[data-testid='publicity-button']"));
        publicityButton.click();
    }

    @And("I am on the friends page")
    public void iAmOnTheFriendsPage() {
        driver.get("http://localhost:8080/friends");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#friends-title")));
    }

    @And("I go to the friends page")
    public void iGoToTheFriendsPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Logout")));
        logoutLink.click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/"));
        driver.get("http://localhost:8080/login");

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("input[type='submit']"));

        emailField.clear();
        emailField.sendKeys("testuser5@example.com");
        passwordField.clear();
        passwordField.sendKeys("testPassword123");
        loginButton.click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/"));
        driver.get("http://localhost:8080/friends");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#friends-title")));
    }

    @Then("I should see {string} on the friends page")
    public void iShouldSeeOnTheFriendsPage(String expectedMessage) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Thread.sleep(1000);
        WebElement friendsTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("friends-title")));

        String actualMessage = friendsTitle.getText();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Then("I should see {string} in the friends list")
    public void iShouldSeeInTheFriendsList(String expectedName) throws InterruptedException {
        Thread.sleep(1000);
        List<WebElement> friendNames = driver.findElements(By.className("card-title"));
        WebElement johnDoeCard = null;

        for (WebElement nameElement : friendNames) {
            if (nameElement.getText().equals(expectedName)) {
                johnDoeCard = nameElement;
                break;
            }
        }

        assertNotNull(johnDoeCard);
    }

    @And("I click on John Doe's card")
    public void iClickOnJohnDoeSCard() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Thread.sleep(1000);
        List<WebElement> friendNames = driver.findElements(By.className("card-title"));
        WebElement johnDoeCard = null;

        for (WebElement nameElement : friendNames) {
            if (nameElement.getText().equals("John Doe")) {
                johnDoeCard = nameElement;
                break;
            }
        }

        assertNotNull(johnDoeCard);
        wait.until(ExpectedConditions.elementToBeClickable(johnDoeCard));
        johnDoeCard.click();
        Thread.sleep(1000);
    }

    @Then("I should see {string} under John Doe's list of favorite parks")
    public void iShouldSeeUnderJohnDoeSListOfFavoriteParks(String parkName) {
        List<WebElement> friendCards = driver.findElements(By.className("friend-item"));
        WebElement johnDoeCard = null;

        for (WebElement card : friendCards) {
            WebElement nameElement = card.findElement(By.className("card-title"));
            if (nameElement.getText().equals("John Doe")) {
                johnDoeCard = card;
                break;
            }
        }

        assertNotNull(johnDoeCard);

        WebElement favoriteParksList = johnDoeCard.findElement(By.tagName("ul"));
        List<WebElement> parkListItems = favoriteParksList.findElements(By.tagName("li"));

        boolean parkFound = parkListItems.stream().anyMatch(li -> li.getText().equals(parkName));
        assertTrue(parkFound);
    }

    @And("I should see {string} under the suggested park")
    public void iShouldSeeUnderTheSuggestedPark(String expectedParkName) {
        List<WebElement> friendCards = driver.findElements(By.className("friend-item"));
        WebElement johnDoeCard = null;

        for (WebElement card : friendCards) {
            WebElement nameElement = card.findElement(By.className("card-title"));
            if (nameElement.getText().equals("John Doe")) {
                johnDoeCard = card;
                break;
            }
        }

        assertNotNull(johnDoeCard);

        WebElement suggestedParkElement = johnDoeCard.findElement(By.className("suggested-park"));
        String actualParkName = suggestedParkElement.getText();

        assertEquals(expectedParkName, actualParkName);
    }

    @Then("I should see {string} under John Doe's card")
    public void iShouldSeeUnderJohnDoeSCard(String expectedMessage) {
        List<WebElement> friendCards = driver.findElements(By.className("friend-item"));
        WebElement johnDoeCard = null;

        for (WebElement card : friendCards) {
            WebElement nameElement = card.findElement(By.className("card-title"));
            if (nameElement.getText().equals("John Doe")) {
                johnDoeCard = card;
                break;
            }
        }

        assertNotNull(johnDoeCard);

        WebElement privateMessage = johnDoeCard.findElement(By.id("private-message"));
        assertEquals(expectedMessage, privateMessage.getText());
    }

    @After
    public void tearDown() {
        purgeUsers();

        if (driver != null) {
            driver.quit();
        }
    }
}
