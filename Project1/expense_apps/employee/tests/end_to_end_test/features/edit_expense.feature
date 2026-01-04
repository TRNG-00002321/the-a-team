Feature: Edit expense
  As an employee
  I want to edit pending expenses
  So that I can correct my mistakes before they are reviewed

Background:
  Given the application is running
  And the test database is already seeded with users
  And the employee is logged in

Scenario Outline: Edit Expense, successful edit
  Given the employee is on my expenses
  And an expense with the description: "Client lunch" is shown
  And the expense with description "Client lunch" is pending
  When the employee clicks the edit button for the expense with description "Client lunch"
  And the employee is redirected to the edit menu
  And the employee inputs into the amount field: "<amount>"
  And the employee inputs into the description field: "<description>"
  And the employee inputs into the date field: "<date>"
  And the employee clicks the update expense button
  Then the employee sees the edit message: "Expense updated successfully!"
  And the expense is updated with the given "<amount>", "<description>", and "<date>"

  Examples:
    | amount      | description          | date                 |
    | 123         | example description  | 2025-12-30           |
    | 999         | fix door             | 2025-10-10           |

Scenario: Edit Expense, cancel edit
  Given the employee is on my expenses
  And an expense with the description: "Client lunch" is shown
  And the expense with description "Client lunch" is pending
  When the employee clicks the edit button for the expense with description "Client lunch"
  And the employee is redirected to the edit menu
  And the employee inputs into the amount field: "999999"
  And the employee inputs into the description field: "wont be updated"
  And the employee inputs into the date field: "2025-12-30"
  And the employee clicks the cancel button
  Then the expense with description "Client lunch" still exists

Scenario: Edit expense, no inputs
  Given the employee is on my expenses
  And an expense with the description: "Client lunch", amount: "50", and date: "2025-01-05"
  When the employee clicks the edit button for the expense with description "Client lunch"
  And the employee clicks the update expense button
  Then the employee sees the edit message: "Expense updated successfully!"
  And the expense is shown with the the amount: "50", description: "Client lunch", and the date: "2025-01-05"