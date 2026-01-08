Feature: Employee Authentication
  As an employee
  I want to login with my credentials
  So that I can securely access my expense reports

Background:
  Given the application is running
  And the test database is already seeded with users

Scenario: Successful Login
  Given the employee is on the login screen
  When the employee enters username "employee1"
  And the employee enters password "password123"
  And the employee clicks the login button
  Then the employee sees the auth message: "Login successful! Redirecting to employee dashboard..."
  And the employee is redirected to the employee dashboard

Scenario Outline: Invalid Login
  Given the employee is on the login screen
  When the employee enters username "<username>"
  And the employee enters password "<password>"
  And the employee clicks the login button
  Then the employee is not redirected to the dashboard
  And the employee sees the auth message: "<message>"

Examples:
    | username    | password             | message              |
    | wronguser   | password123          | Invalid credentials  |
    | employee1   | wrongpassword        | Invalid credentials  |
    | wronguser   | wrongpassword        | Invalid credentials  |
    | manager1    | password123          | Login failed         |

Scenario: Empty Username Input
  Given the employee is on the login screen
  When the employee does not input any value for username
  And the employee clicks the login button
  Then the username field is selected
  And the employee is not redirected to the dashboard

Scenario: Empty Password Input
  Given the employee is on the login screen
  And the employee enters username "employee1"
  When the employee does not input any value for the password
  And the employee clicks the login button
  Then the password field is selected
  And the employee is not redirected to the dashboard

Scenario: Logout
  Given the employee is logged in
  When the employee clicks the logout button
  Then the employee is redirected to the login page

