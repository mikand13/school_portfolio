Feature:
  As a customer
  I want to log in
  so that <business gain>

  Scenario: Log in with correct credentials
    Given that I am on the login page
    When I enter correct login credentials
    Then I should be logged in

  Scenario: Log in with incorrect credentials
    Given that I am on the login page
    When I enter incorrect login credentials
    Then I should not be logged in