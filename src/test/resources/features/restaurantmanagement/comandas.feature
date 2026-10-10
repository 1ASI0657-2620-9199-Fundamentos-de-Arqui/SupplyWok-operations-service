Feature: Restaurant Comanda Lifecycle Management
  As a restaurant staff member
  I want to create, update, and manage table comandas
  So that food orders are tracked accurately and tables are occupied and released correctly

  Scenario: Opening a comanda on an available table occupies the table
    Given a table with ID 1 exists for restaurant 5 in status "AVAILABLE"
    When restaurant 5 creates a comanda on table 1
    Then the comanda is successfully created with status "OPEN"
    And table 1 status is updated to "OCCUPIED"

  Scenario: Opening a comanda on a table belonging to another restaurant is rejected
    Given a table with ID 2 exists for restaurant 9 in status "AVAILABLE"
    When restaurant 5 attempts to create a comanda on table 2
    Then the comanda creation is rejected as table not found

  Scenario: Closing a comanda releases the table back to available
    Given an open comanda with ID 50 exists on table 1 for restaurant 5
    When restaurant 5 updates the comanda status to "CLOSED"
    Then the comanda status is updated to "CLOSED"
    And table 1 status is updated to "AVAILABLE"

  Scenario: Updating a comanda with an invalid status is rejected
    Given an open comanda with ID 50 exists on table 1 for restaurant 5
    When restaurant 5 attempts to update the comanda status using invalid status value "NON_EXISTENT_STATUS"
    Then the comanda status update is rejected with an invalid status error
