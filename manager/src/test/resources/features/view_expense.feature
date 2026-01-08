Feature: View Expenses
  As a manager
  I want to view all expenses with a filtering option
  So that I can review them efficiently

Background:
  Given the application is running
  And the test database is already seeded with users
  And the manager is logged in

Scenario: View all expenses with show all button
  Given the manager is on the all expenses screen
  When the manager clicks the show all button
  Then all expenses are shown

Scenario: View all expenses with refresh button
  Given the manager is on the all expenses screen
  When the manager inputs "999" for the employee id
  And the manager clicks the filter button
  And the manager clicks the refresh button
  Then all expenses are shown

Scenario Outline: View expense based on employee id, positive test
  Given the manager is on the all expenses screen
  When the manager inputs "<id>" for the employee id
  And the manager clicks the filter button
  Then the manager is shown "<count>" expenses for user "<id>"

  Examples:
    | id   | count    |
    | 1    | 3        |
    | 2    | 2        |

Scenario: View expense based on employee id, employee does not exist
  Given the manager is on the all expenses screen
  When the manager inputs "999" for the employee id
  And the manager clicks the filter button
  Then the manager is shown the message: "No expenses found."














