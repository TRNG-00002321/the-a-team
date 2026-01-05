Feature: View expenses
  As an employee
  I want to view expense history and corresponding status
  So that I can track my financial activity overtime and know whether expenses are pending, approved, or denied

Background:
  Given the application is running
  And the test database is already seeded with users
  And the employee is logged in

  #ES-228
Scenario: View all expenses
  Given the employee is on my expenses
  When the filter by status option is selected for all
  Then all expenses are shown

  #ES-229
Scenario Outline: View expenses by status
  Given the employee is on my expenses
  When the filter by status option <status> is selected
  Then all expenses only with status: <status> are shown

  Examples:
    | status   |
    | Pending  |
    | Approved |
    | Denied   |

#Stretch goal
  #Scenario: Check expense status updated with refresh button

