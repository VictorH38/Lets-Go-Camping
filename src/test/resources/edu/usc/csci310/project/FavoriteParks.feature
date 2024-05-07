@Include
Feature: test the functionality of the favorites page
  Scenario: displaying no favorite parks
    Given I am on the favorites page
    And I haven't added any parks to favorite
    Then I should see "You have no favorite parks" on the favorites page
  Scenario: successfully displaying favorite parks
    Given I have added Grand Canyon National Park to my favorites list
    And I go to the favorites page
    Then I should see the "Grand Canyon National Park" card on the favorites page
  Scenario: successfully toggling profile publicity
    Given I am on the favorites page
    And the publicity button says "Public"
    When I click on the publicity button
    Then the publicity button should change to "Private"
  Scenario: successfully updating favorites page after removing all favorite parks
    Given I have added Grand Canyon National Park to my favorites list
    And I go to the favorites page
    And I remove "Grand Canyon National Park" from my favorites
    Then I should see "You have no favorite parks" on the favorites page
  Scenario: successfully rearranging favorite parks
    Given I have added Grand Canyon National Park to my favorites list
    And I have added Yosemite National Park to my favorites list
    When I go to the favorites page
    And Grand Canyon National Park is above Yosemite National Park on my favorites list
    And I drag Grand Canyon National Park below Yosemite National Park on my favorites list
    Then I should see Grand Canyon National Park below Yosemite National Park on my favorites list
