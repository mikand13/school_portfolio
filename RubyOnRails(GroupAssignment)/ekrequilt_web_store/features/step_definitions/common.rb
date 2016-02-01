Given(/^that I am not logged in$/) do
  page.driver.delete destroy_account_session_path # make sure user is logged out
  page.driver.delete destroy_admin_account_session_path # make sure any admin account is logged out
end

Given(/^that I am logged on as a store manager$/) do
  page.driver.delete destroy_account_session_path
  page.driver.delete destroy_admin_account_session_path

  visit new_admin_account_session_path
  fill_in 'admin_account_email', with: 'store_manager@ekrequilt.no'
  fill_in 'admin_account_password', with: 'dev'
  click_button I18n.t('devise.login')

  refute page.has_content? I18n.t('devise.failure.not_found_in_database')
  assert page.has_content? I18n.t('devise.admin_users_sessions.admin_account.signed_in')
end

Given(/^that I am logged in as a customer$/) do
  page.driver.delete destroy_account_session_path
  page.driver.delete destroy_admin_account_session_path

  visit root_path
  click_link I18n.t('devise.login')
  within(:css, '#new_account') do
    fill_in 'account_email', with: 'strpia13@ekrequilt.no'
    fill_in 'account_password', with: 'dev'
    click_button I18n.t('devise.login')
  end
end