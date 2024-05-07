package edu.usc.csci310.project.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;

import static edu.usc.csci310.project.WebDriverUtil.initializeDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserAccountCreationStep {
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
                ps.setString(1, "testuser1@example.com");
                ps.executeUpdate();
            }
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");

            Statement stmt2 = conn.createStatement();
            stmt2.execute("SET FOREIGN_KEY_CHECKS = 0;");
            String sql2 = "DELETE FROM user WHERE email = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                ps2.setString(1, "takenemail@example.com");
                ps2.executeUpdate();
            }
            stmt2.execute("SET FOREIGN_KEY_CHECKS = 1;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {
        driver = initializeDriver();
        try {
            Thread.sleep(1_000);
        } catch (Exception e) {}
    }

    @Given("I am on the create account page")
    public void i_am_on_the_create_account_page() {
        driver.get("http://localhost:8080/signup");
    }

    @When("I enter an name")
    public void i_enter_an_name() {
        driver.findElement(By.id("name")).sendKeys("test name");
    }


    @And("I enter an available and valid email")
    public void i_enter_an_available_and_valid_email() {
        driver.findElement(By.id("email")).sendKeys("testuser1@example.com");
    }

    @When("I enter an email that is taken")
    public void i_enter_an_email_that_is_taken() {
        try {
            HttpClient client = HttpClient.newHttpClient();

            String requestBody = """
            {
                "name": "Test User",
                "email": "takenemail@example.com",
                "password": "password123"
            }
            """;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/users/signup"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create user", e);
        }

        driver.findElement(By.id("email")).sendKeys("takenemail@example.com");
    }

    @When("I enter a valid password")
    public void i_enter_a_valid_password() {
        driver.findElement(By.id("password")).sendKeys("password123");
    }

    @When("I enter a valid password confirmation")
    public void i_enter_a_valid_password_confirmation() {
        driver.findElement(By.id("passwordConfirmation")).sendKeys("password123");
    }

    @When("I enter a invalid password confirmation")
    public void i_enter_a_invalid_password_confirmation() {
        driver.findElement(By.id("passwordConfirmation")).sendKeys("differentPassword");
    }

    @When("I press the Create Account button")
    public void i_press_the_create_account_button() {
        driver.findElement(By.cssSelector("input[type='submit']")).click();
    }

    @Then("I should get a {string} message")
    public void i_should_get_a_message(String message) throws InterruptedException {
        Thread.sleep(2000);
        assertEquals(message, driver.findElement(By.id("error")).getText());
    }

    @Then("I should get redirected to the index page")
    public void i_should_get_redirected_to_the_index_page() throws InterruptedException {
        Thread.sleep(2000);
        String currentUrl = driver.getCurrentUrl();

        assertEquals("http://localhost:8080/", currentUrl);
    }

    @After
    public void tearDown() {
        purgeUsers();

        if (driver != null) {
            driver.quit();
        }
    }
}

