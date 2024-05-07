@Include
Feature: Searching for National Park
  Scenario: Successfully Searching for Park By Name
    Given I am on the "Search" page
    When The user enters "Yosemite National Park" into the searchbar
    And User clicks the search button
    Then I should see "1 Result(s)"
  Scenario: Unsuccessfully Searching for Park
    Given I am on the "Search" page
    When The user enters "Yugoslavia" into the searchbar
    And User clicks the search button
    Then I should see "No results for \"Yugoslavia\"" message
  Scenario: Refreshing Search Page
    Given I am on the "Search" page
    When The user enters "forest" into the searchbar
    And User clicks the search button
    And I see "2 Result(s)"
    And The user enters "Yosemite National Park" into the searchbar
    And User clicks the search button
    Then I should see "1 Result(s)"
  Scenario: Retrieving All Parks
    Given I am on the "Search" page
    When The user enters "" into the searchbar
    And User clicks the search button
    Then I should see "471 Result(s)"
  Scenario: Filter Park By State
    Given I am on the "Search" page
    When The user enters "" into the searchbar
    And User clicks the search button
    And The user filters by state to limit results to parks in "California"
    Then I should see "34 Result(s)"
  Scenario: Filter Park By Activity
    Given I am on the "Search" page
    When The user enters "" into the searchbar
    And User clicks the search button
    And The user filters by activity to limit results to parks with "Fishing"
    Then I should see "144 Result(s)"
  Scenario: Filter Park By Amenity
    Given I am on the "Search" page
    When The user enters "" into the searchbar
    And User clicks the search button
    And The user filters by amenity to limit results to parks with "Braille"
    Then I should see "96 Result(s)"
  Scenario: Filter Park By Amenity and State
    Given I am on the "Search" page
    When The user enters "" into the searchbar
    And User clicks the search button
    And The user filters by state to limit results to parks in "California"
    And The user filters by amenity to limit results to parks with "Braille"
    Then I should see "5 Result(s)"
  Scenario: Filter Park By Amenity, Activity and State
    Given I am on the "Search" page
    When The user enters "" into the searchbar
    And User clicks the search button
    And The user filters by state to limit results to parks in "California"
    And The user filters by amenity to limit results to parks with "Braille"
    And The user filters by activity to limit results to parks with "Fishing"
    Then I should see "1 Result(s)"
  Scenario: Filter Park By Amenity and Activity
    Given I am on the "Search" page
    When The user enters "" into the searchbar
    And User clicks the search button
    And The user filters by amenity to limit results to parks with "Braille"
    And The user filters by activity to limit results to parks with "Fishing"
    Then I should see "25 Result(s)"
  Scenario: Filter Park By Activity and State
    Given I am on the "Search" page
    When The user enters "" into the searchbar
    And User clicks the search button
    And The user filters by state to limit results to parks in "California"
    And The user filters by activity to limit results to parks with "Fishing"
    Then I should see "10 Result(s)"
