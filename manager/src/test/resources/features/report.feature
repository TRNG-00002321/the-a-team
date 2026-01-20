Feature: Report Feature
  As a manager
  I want to generate reports by employee, category, or date
  So that I can analyze spending trends and make informed decisions

  Background:
    Given the application is running
    And the test database is already seeded with users
    And the manager is logged in
    And the manager is on the generate reports screen

  Scenario Outline: Simple Report Generation
    When the manager clicks the <button> button
    Then a report is downloaded, and a "Report generated successfully!" message is shown

    Examples:
      | button                      |
      | all expenses report         |
      | pending expenses report     |

  Scenario: Employee Report Generation
    When the manager inputs "1" into the report employee id field
    And the manager clicks the "Generate Employee Report" report button
    Then a report is downloaded, and a "Report generated successfully!" message is shown

  Scenario: Category Report Generation
    When the manager inputs "Travel" into the report category field
    And the manager clicks the "Generate Category Report" report button
    Then a report is downloaded, and a "Report generated successfully!" message is shown

  Scenario: Date Range Report Generation
    When the manager inputs "01/01/2025" into the start date field
    And the manager inputs "12/31/2025" into the end date field
    And the manager clicks the "Generate Date Range Report" report button
    Then a report is downloaded, and a "Report generated successfully!" message is shown

  Scenario Outline: Report Generation Validation
    Given report all input fields are empty
    When the manager clicks the "<button>" report button
    Then a message containing: "<message>" is shown

    Examples:
      | button                         | message                                |
      | Generate Employee Report       | Please enter an employee ID            |
      | Generate Category Report       | Please enter a category                |
      | Generate Date Range Report     | Please select both start and end dates |