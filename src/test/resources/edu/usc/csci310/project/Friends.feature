@Include
Feature: test the functionality of the friends page
  Scenario: displaying no friends
    Given no other users have signed up
    And I am on the friends page
    Then I should see "You have no friends" on the friends page
  Scenario: successfully displaying friends
    Given another user signs up with the name "John Doe"
    And I go to the friends page
    Then I should see "John Doe" in the friends list
  Scenario: expanding friend details
    Given another user signs up with the name "John Doe"
    And John Doe adds Grand Canyon National Park to their favorites
    And I add Grand Canyon National Park to my favorites
    And I go to the friends page
    And I click on John Doe's card
    Then I should see "Grand Canyon National Park" under John Doe's list of favorite parks
    And I should see "Grand Canyon National Park" under the suggested park
  Scenario: expanding friend details with no suggested park
    Given another user signs up with the name "John Doe"
    And John Doe adds Grand Canyon National Park to their favorites
    And I add Yosemite National Park to my favorites
    And I go to the friends page
    And I click on John Doe's card
    Then I should see "Grand Canyon National Park" under John Doe's list of favorite parks
    And I should see "You have no common favorite parks to suggest" under the suggested park
  Scenario: expanding private friend details
    Given another user signs up with the name "John Doe"
    And John Doe adds Grand Canyon National Park to their favorites
    And John Doe toggles their profile publicity to private
    And I go to the friends page
    And I click on John Doe's card
    Then I should see "Favorite parks are private" under John Doe's card
