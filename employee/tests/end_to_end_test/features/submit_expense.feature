Feature: Submit new expense
  As an employee
  I want to submit new expenses with details about amount and description
  So that I can request reimbursement or track spending

Background:
  Given the application is running
  And the test database is already seeded with users
  And the employee is logged in

Scenario Outline: Successful expense submit
  Given the employee is at the submit expense menu
  When the employee inputs a new amount: "<amount>"
  And the employee inputs a new description: "<description>"
  And the employee inputs a new date: "<date>"
  And the employee clicks the submit expense button
  Then the employee sees the message: "Expense submitted successfully!"
  And the employee navigates to the expenses screen
  And the expense is shown with the amount: "<amount>", description: "<description>", and the date: "<date>"

  Examples:
    | amount | description         | date       |
    | 123    | example description | 2025-12-31 |
    | 999    | fix door            | 2025-01-01 |

  Scenario: Submit expense with empty amount
  Given the employee is at the submit expense menu
  When the amount field is empty
  And the employee clicks the submit expense button
  Then the amount field is selected
  And the employee stays on the submit menu screen

Scenario: Submit expense with empty description
  Given the employee is at the submit expense menu
  When the employee inputs a new amount: "125"
  And the description field is empty
  And the employee clicks the submit expense button
  Then the description field is selected
  And the employee stays on the submit menu screen

Scenario: Submit expense without inputting date
  Given the employee is at the submit expense menu
  When the employee inputs a new amount: "100"
  And the employee inputs a new description: "todays date"
  And the employee clicks the submit expense button
  Then the employee sees the message: "Expense submitted successfully!"
  And the employee navigates to the expenses screen
  And an expense with today's date, amount: "100" and description: "todays date" is shown
