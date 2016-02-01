And(/^that I am on the login page$/) do
  visit root_path
  click_link I18n.t('devise.login')
end

When(/^I enter correct login credentials$/) do
  within(:css, '#new_account') do
    fill_in 'account_email', with: 'strpia13@ekrequilt.no'
    fill_in 'account_password', with: 'dev'
    click_button I18n.t('devise.login')
  end
end

Then(/^I should be logged in$/) do
  assert find('#message').has_content? I18n.t('devise.users_sessions.account.signed_in')
  page.assert_no_selector(:css, '#sign-in-button')
end

When(/^I enter incorrect login credentials$/) do
  within(:css, '#new_account') do
    fill_in 'account_email', with: 'strpia13@ekrequilt.no'
    fill_in 'account_password', with: 'whowouldeverusethispasswordasapassword'
    click_button I18n.t('devise.login')
  end
end

Then(/^I should not be logged in$/) do
  assert find('#message').has_content? I18n.t('devise.failure.invalid')
  refute find('#message').has_content? I18n.t('devise.users_sessions.account.signed_in')
end
