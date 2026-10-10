Feature: Alerts Management and Cross-Service Integration
  As a restaurant operator or supplier
  I want integration events to trigger operational alerts with strict tenant isolation
  So that I only receive relevant notifications for my business

  Scenario: Inventory minimum stock reached integration event generates a high severity restaurant alert
    Given a restaurant with ID 10
    When an inventory minimum stock event occurs for supply "Jasmine Rice" with stock 5 and minimum 20 for restaurant 10
    Then a HIGH severity alert is created for restaurant 10
    And the alert is visible to restaurant 10
    And the alert is not visible to restaurant 20

  Scenario: Purchase order created integration event generates an alert for the designated supplier
    Given a supplier with email "supplier@freshfoods.com"
    When a purchase order created event is received for order "PO-1001" from restaurant "Dragon Palace" with priority "High" for supplier "supplier@freshfoods.com"
    Then a HIGH severity alert is created for supplier "supplier@freshfoods.com"
    And the alert is visible to supplier "supplier@freshfoods.com"
    And the alert is not visible to supplier "other@vendor.com"

  Scenario: Restaurant successfully acknowledges a pending alert
    Given a pending alert exists for restaurant 10
    When restaurant 10 acknowledges the alert
    Then the alert status is changed to ACKNOWLEDGED

  Scenario: Restaurant cannot acknowledge an alert belonging to another restaurant
    Given a pending alert exists for restaurant 10
    When restaurant 20 attempts to acknowledge the alert of restaurant 10
    Then the alert acknowledgment is rejected as not found
