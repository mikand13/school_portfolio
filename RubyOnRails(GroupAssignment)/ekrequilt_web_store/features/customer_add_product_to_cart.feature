Feature:
  As a customer
  I want to be able to add products to a shopping cart,
  so I can keep track of what products I want to buy.

  Scenario: Customer adds products to cart
    Given that I am logged in as a customer
    When I select the products I want to buy
    Then I should see all the products I have selected in my shopping cart