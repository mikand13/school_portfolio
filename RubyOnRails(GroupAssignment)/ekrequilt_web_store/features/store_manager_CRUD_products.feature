Feature:
  As the store manager
  I want to be able to manage products
  so that I can keep the store up to date

  Scenario: Create a product
    Given that I am logged on as a store manager
    And that I am on the product creation page
    When I fill in the product information
    Then I should see the information for the created product

  Scenario: Show a product
    Given that I am logged on as a store manager
    And a new product has just been created
    When I look up the most recent created product
    Then I should see the information for the most recent created product

  Scenario: Update a product
    Given that I am logged on as a store manager
    And a product with values that needs to be changed
    And I enter the products edit page
    When I correct the incorrect fields
    Then the product should have its values updated
