Feature: Restaurant Table Lifecycle and Tenant Isolation
  As a restaurant manager
  I want to maintain tables and update their status within my restaurant
  So that table availability is kept up to date and protected from other restaurants

  Scenario: Successfully update table status within the same restaurant
    Given a restaurant table with ID 10 exists for restaurant 5 in status "AVAILABLE"
    When restaurant 5 updates the status of restaurant table 10 to "RESERVED"
    Then restaurant table 10 status is updated to "RESERVED"

  Scenario: Updating status of a table belonging to another restaurant is rejected
    Given a restaurant table with ID 11 exists for restaurant 8 in status "AVAILABLE"
    When restaurant 5 attempts to update the status of restaurant table 11 to "OCCUPIED"
    Then the table status update is rejected as table not found

  Scenario: Deleting a table belonging to another restaurant is rejected
    Given a restaurant table with ID 12 exists for restaurant 8
    When restaurant 5 attempts to delete restaurant table 12
    Then the table deletion is rejected as table not found

  Scenario: Updating table status with an invalid status is rejected
    Given a restaurant table with ID 10 exists for restaurant 5 in status "AVAILABLE"
    When restaurant 5 attempts to update restaurant table 10 status using invalid status value "BROKEN_STATUS"
    Then the table status update is rejected with an invalid status error
