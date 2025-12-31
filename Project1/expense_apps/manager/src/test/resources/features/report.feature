Feature: Report Feature
  As a manager
  I want to generate reports by employee, category, or date
  So that I can analyze spending trends and make informed decisions

Background:
  Given the application is running
  And the test database is already seeded with users
  And the manager is logged in

Scenario: All Expenses Report
  Given the manager is on the generate reports screen
  When the manager clicks the all expenses report button
  Then a report is downloaded, and a "Report generated successfully!" message is shown

Scenario: Pending Expenses Report
  Given the manager is on the generate reports screen
  When the manager clicks the pending expenses report button
  Then a report is downloaded, and a "Report generated successfully!" message is shown

Scenario Outline: Report generation without inputting values
  Given the manager is on the generate reports screen
  And report all input fields are empty
  When the manager clicks the "<button>" button
  Then a message containing: "<message>" is shown

  Examples:
    | button                         | message                                |
    | Generate Employee Report       | Please enter an employee ID            |
    | Generate Category Report       | Please enter a category                |
    | Generate Date Range Report     | Please select both start and end dates |

Scenario: Successful employee report
  Given the manager is on the generate reports screen
  When the manager inputs "1" into the report employee id field
  When the manager clicks the "Generate Employee Report" button
  Then a report is downloaded, and a "Report generated successfully!" message is shown

Scenario: Category Report
  Given the manager is on the generate reports screen
  When the manager inputs "Travel" into the report category field
  When the manager clicks the "Generate Category Report" button
  Then a report is downloaded, and a "Report generated successfully!" message is shown

Scenario: Date Range Report
  Given the manager is on the generate reports screen
  When the manager inputs "2025-01-01" into the start date field
  And the manager inputs "2025-12-31" into the end date field
  And the manager clicks the "Generate Date Range Report" button
  Then a report is downloaded, and a "Report generated successfully!" message is shown
