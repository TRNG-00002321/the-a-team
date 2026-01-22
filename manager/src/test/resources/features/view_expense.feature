Feature: View Expenses
  As a manager
  I want to view all expenses with a filtering option
  So that I can review them efficiently

  Background:
    Given the application is running
    And the test database is already seeded with users
    And the manager is logged in
    And the manager is on the all expenses screen

  Scenario: View All Expenses With Show All Button
    When the manager clicks the show all button
    Then all expenses are shown

  Scenario: View All Expenses With Refresh Button
    When the manager inputs "999" for the employee id
    And the manager clicks the filter button
    And the manager clicks the refresh button
    Then all expenses are shown

  Scenario Outline: Filter Expenses By Employee ID
    When the manager inputs "<id>" for the employee id
    And the manager clicks the filter button
    Then the manager is shown "<count>" expenses for user "<id>"

    Examples:
      | id  | count |
      | 1   | 3     |
      | 2   | 2     |

  Scenario: Filter With Non-Existent Employee
    When the manager inputs "999" for the employee id
    And the manager clicks the filter button
    Then the manager is shown the message: "No expenses found."