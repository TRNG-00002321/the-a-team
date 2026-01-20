Feature: Manager Authentication
  As a manager
  I want to login securely
  So that I can access and manage manager expense reports

  Background:
    Given the application is running
    And the test database is already seeded with users

  #MS-230, MS-231
  Scenario Outline: Manager Login Attempts
    Given the manager is on the login screen
    When the manager enters username "<username>"
    And the manager enters password "<password>"
    And the manager clicks the login button
    Then the manager sees the message: "<message>"
    And the manager <redirect_status> redirected to the manager dashboard

    Examples:
      | username    | password        | message                                                      | redirect_status |
      | manager1    | password123     | Login successful! Redirecting to manager dashboard...       | is              |
      | wronguser   | password123     | Invalid credentials or user is not a manager                | is not          |
      | manager1    | wrongpassword   | Invalid credentials or user is not a manager                | is not          |
      | wronguser   | wrongpassword   | Invalid credentials or user is not a manager                | is not          |
      | employee1   | password123     | Invalid credentials or user is not a manager                | is not          |

  #MS-232, MS-233
  Scenario Outline: Empty Field Validation
    Given the manager is on the login screen
    When the manager enters username "<username>"
    And the manager enters password "<password>"
    And the manager clicks the login button
    Then the <field> field is selected
    And the manager is not redirected to the dashboard

    Examples:
      | username  | password    | field    |
      |           | password123 | username |
      | manager1  |             | password |

  #MS-234
  Scenario: Logout
    Given the manager is logged in
    When the manager clicks the logout button
    Then the manager is redirected to the login page