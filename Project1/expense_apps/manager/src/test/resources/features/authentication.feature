Feature: Manager Authentication
  As a manager
  I want to login securely
  So that I can access and manage manager expense reports

Background:
  Given the application is running
  And the test database is already seeded with users

Scenario: Successful Login
  Given the manager is on the login screen
  When the manager enters username "manager1"
  And the manager enters password "password123"
  And the manager clicks the login button
  Then the manager sees the message: "Login successful! Redirecting to manager dashboard..."
  And the manager is redirected to the manager dashboard

Scenario Outline: Invalid Login
  Given the manager is on the login screen
  When the manager enters username "<username>"
  And the manager enters password "<password>"
  And the manager clicks the login button
  Then the manager is not redirected to the dashboard
  And the manager sees the message: "Invalid credentials or user is not a manager"

  Examples:
    | username    | password             |
    | wronguser   | password123          |
    | manager1    | wrongpassword        |
    | wronguser   | wrongpassword        |
    | employee1   | password123          |

Scenario: Empty Username Input
  Given the manager is on the login screen
  When the manager does not input any value for username
  And the manager clicks the login button
  Then the username field is selected
  And the manager is not redirected to the dashboard

Scenario: Empty Password Input
  Given the manager is on the login screen
  And the manager enters username "manager1"
  When the manager does not input any value for the password
  And the manager clicks the login button
  Then the password field is selected
  And the manager is not redirected to the dashboard

Scenario: Logout
  Given the manager is logged in
  When the manager clicks the logout button
  Then the manager is redirected to the login page