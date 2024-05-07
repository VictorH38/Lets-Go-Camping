@Include
Feature: test the functionality of the park details
  Scenario: viewing park details for Grand Canyon
    Given I am on the park search page
    And I search for "Grand Canyon National Park"
    And I click on the search button
    Then I should see a title that says "Grand Canyon National Park"
  Scenario: expanding park details
    Given I am on the park search page
    When I search for "Grand Canyon National Park"
    And I click on the search button
    And I click on the "Grand Canyon National Park" card
    Then I should see the expanded details of the park
