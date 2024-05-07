@Include
Feature: Creating User Account
  Scenario: A normal successful account creation
    Given I am on the create account page
    When I enter an name
    And I enter an available and valid email
    And I enter a valid password
    And I enter a valid password confirmation
    And I press the Create Account button
    Then I should get redirected to the index page
  Scenario: Email that is already taken
    Given I am on the create account page
    When I enter an name
    And I enter an email that is taken
    And I enter a valid password
    And I enter a valid password confirmation
    And I press the Create Account button
    Then I should get a "Email already in use" message
  Scenario: Password Confirmation not matching
    Given I am on the create account page
    When I enter an name
    And I enter an available and valid email
    And I enter a valid password
    And I enter a invalid password confirmation
    And I press the Create Account button
    Then I should get a "Passwords don't match" message
