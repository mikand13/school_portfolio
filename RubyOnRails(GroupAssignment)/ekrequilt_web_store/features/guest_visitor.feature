Feature:
  As a guest
  I want to visit the Ekrequilt homepage
  so that I can view all the awesome stuff.

  Scenario: Guest visits index page
    Given a user not logged in
    When the user visits the index page
    Then the user can see a login button
    And the user can not see a log out button