Given(/^a user not logged in$/) do
  page.driver.delete destroy_account_session_path
end

When(/^the user visits the index page$/) do
  visit root_path
end

Then(/^the user can see a login button$/) do
  assert page.has_css? "a[href=\"/accounts/sign_in\"]"
end

And(/^the user can not see a log out button$/) do
  assert_not page.has_css? "a[href=\"/accounts/sign_out\"]"
end