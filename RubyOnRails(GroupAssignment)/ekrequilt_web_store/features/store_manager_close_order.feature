Feature:
  As the store manager
  I want to be able to close an order
  so it will no longer register as open.

  Scenario: Close an order
    Given that I am logged on as a store manager
    And that I am on the show page of a order that is open
    When I confirm the order
    Then the order should be closed