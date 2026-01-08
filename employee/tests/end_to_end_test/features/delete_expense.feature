Feature: Delete Expense
  As an employee
  I want to delete pending expenses
  So that I can correct my mistakes before they are reviewed

Background:
  Given the application is running
  And the test database is already seeded with users
  And the employee is logged in

Scenario: Successful Delete Expense
  Given the employee is on my expenses
  And an expense with the description: "Client lunch" is shown
  And the expense with description "Client lunch" is pending
  When the employee clicks the delete button for the expense with description "Client lunch"
  And the employee clicks ok for the confirmation alert
  And the employee is shown another alert with message: "Expense deleted successfully!"
  And the employee clicks ok to close the alert
  Then the expense with description: "Client lunch" is no longer shown

Scenario: Cancel Delete Expense
  Given the employee is on my expenses
  And an expense with the description: "Travel Expenses" is shown
  And the expense with description "Travel Expenses" is pending
  When the employee clicks the delete button for the expense with description "Travel Expenses"
  And the employee clicks cancel for the confirmation alert
  Then the expense with description: "Travel Expenses" is still shown



