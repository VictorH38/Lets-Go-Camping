@Include
Feature: test the functionality of the favorite button on the search page
  Scenario: adding a park to favorites
    Given I am on the park search page
    And I search for "Grand Canyon National Park"
    And I click on the search button
    And I click on the favorites button
    Then the favorites button should change to "Remove from Favorites"
  Scenario: removing a park from favorites after saying yes to confirmation
    Given I am on the park search page
    When I search for "Grand Canyon National Park"
    And I click on the search button
    And I click on the favorites button
    And I click on the favorites button again
    And I see the remove from favorites confirmation
    And I click yes
    Then the favorites button should change to "Add to Favorites"
  Scenario: keeping a park in favorites after saying no to confirmation
    Given I am on the park search page
    When I search for "Grand Canyon National Park"
    And I click on the search button
    And I click on the favorites button
    And I click on the favorites button again
    And I see the remove from favorites confirmation
    And I click no
    Then the favorites button should still say "Remove from Favorites"
