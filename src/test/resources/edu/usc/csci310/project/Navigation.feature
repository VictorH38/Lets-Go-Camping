@Include
Feature: test the functionality of the navigation bar
  Scenario: successfully navigating to home page
    Given I am on the home page
    And I click the "Search" button in the navigation bar
    And I am taken to the search page
    When I click the "Home" button in the navigation bar
    Then I should be taken to the home page
  Scenario: successfully navigating to search page
    Given I am on the home page
    When I click the "Search" button in the navigation bar
    Then I should be taken to the "search" page
  Scenario: successfully navigating to Favorites page
    Given I am on the home page
    When I click the "Favorites" button in the navigation bar
    Then I should be taken to the "favorites" page
  Scenario: successfully navigating to friends page
    Given I am on the home page
    When I click the "Friends" button in the navigation bar
    Then I should be taken to the "friends" page
  Scenario: successfully navigating to login page
    Given I am on the home page
    And I log out successfully
    When I click the "Login" button in the navigation bar
    Then I should be taken to the "login" page
