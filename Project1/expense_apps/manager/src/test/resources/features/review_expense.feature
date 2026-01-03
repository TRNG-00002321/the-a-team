Feature: Review Expenses
  As a manager
  I want to view a list of all pending expenses and approve or deny them with comments
  So that I can manage reimbursements efficiently

Background:
  Given the application is running
  And the test database is already seeded with users
  And the manager is logged in

Scenario Outline: Review expense
  Given the manager is on the pending expenses screen
  And an expense with id: "1", date: "2025-01-05", amount: "50", and description: "Client lunch" is pending
  When the manager clicks the review button for an expense with id: "1", date: "2025-01-05", amount: "50", and description: "Client lunch"
  And the manager inputs in the comment box: "<comment>"
  And the manager clicks the "<action>" button
  Then the manager sees a message containing "<message>"
  And the manager clicks the all expenses button
  And an expense with id: "1", date: "2025-01-05", amount: "50", and description: "Client lunch" is shown as "<action>", with comment "<comment>"

  Examples:
    | action      | message                           | comment          |
    | Approve     | Expense approved successfully!    |                  |
    | Deny        | Expense denied successfully!      |                  |
    | Approve     | Expense approved successfully!    | approved comment |
    | Deny        | Expense denied successfully!      | denied comment   |

Scenario: Cancel expense review
  Given the manager is on the pending expenses screen
  And an expense with id: "1", date: "2025-01-05", amount: "50", and description: "Client lunch" is pending
  When the manager clicks the review button for an expense with id: "1", date: "2025-01-05", amount: "50", and description: "Client lunch"
  And the manager cancels the review
  Then the manager should stay on the pending expenses screen
  And an expense with id: "1", date: "2025-01-05", amount: "50", and description: "Client lunch" is pending
