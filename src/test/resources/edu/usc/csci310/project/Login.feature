@Include
Feature: test login functionality
  Scenario: User enters correct credentials
  Given I am on the login page
  When I enter valid credentials
  And I press the login button
  Then I should get logged in
Scenario: User enters incorrect credentials
  Given I am on the login page
  When I enter invalid credentials
  And I press the login button
  Then I should get a "Invalid password. 3 attempts remaining in the next minute." message on the login page
Scenario: User enters incorrect credentials, gets locked out
  Given I am on the login page
  When I enter invalid credentials
  And I press the login button
  And I press the login button
  And I press the login button
  And I press the login button
  Then I should get a "Account is temporarily locked" message on the login page
Scenario: User re-attempts login after lockout
  Given I am on the login page
  When I enter invalid credentials
  And I press the login button
  And I press the login button
  And I press the login button
  And I press the login button
  Then I wait a minute before the next attempt
  And I press the login button
  Then I should get a "Invalid password. 3 attempts remaining in the next minute." message on the login page
