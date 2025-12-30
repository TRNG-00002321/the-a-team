Feature: View Expenses
  As a manager
  I want to view all expenses with a filtering option
  So that I can review them efficiently

Background:
  Given the application is running
  And the test database is already seeded with users
  And the manager is logged in

Scenario: View all expenses
  Given the manager is on the all expenses screen
