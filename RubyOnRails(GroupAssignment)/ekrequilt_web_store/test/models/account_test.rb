require 'test_helper'

class AccountTest < ActiveSupport::TestCase
  def setup
    @account = FactoryGirl.create(:account)
    @faulty_account = FactoryGirl.build(:faulty_account)
    @faulty_account_no_pass = FactoryGirl.build(:faulty_account_no_pass)
  end

  def teardown
    @account.destroy if @account
  end

  test 'validates_correctly' do
    assert @account, 'Valid Account not validated correctly'
    assert_not @faulty_account.valid?, 'Unvalid account validated'
  end

  test 'self.find_by_confirmation_token' do
    token = 'this_is_a_bogus_token'
    token_complete_bogus = 'this_is_a_non_factory_bogus_token'
    assert Account.find_by_confirmation_token(token), 'Account not found'
    assert_not Account.find_by_confirmation_token(token_complete_bogus), 'Account found'
  end

  test 'self.find_by_confirmation_token!' do
    token = 'this_is_a_bogus_token'
    token_complete_bogus = 'this_is_a_non_factory_bogus_token'
    assert Account.find_by_confirmation_token!(token), 'Account not found'
    assert_not Account.find_by_confirmation_token!(token_complete_bogus), 'Account found'
  end

  test 'attempt_set_password' do
    assert @account.password.eql?('this_124_IS_valid!'), 'Password does not match'
    assert @account.password_confirmation.eql?('this_124_IS_valid!'), 'Password_confirmation does not match'

    @account.attempt_set_password(password: 'this_IS_123_also_valid', password_confirmation: 'this_IS_123_also_valid')

    assert @account.password.eql?('this_IS_123_also_valid'), 'Password does not match'
    assert @account.password_confirmation.eql?('this_IS_123_also_valid'), 'Password_confirmation does not match'

    @account.attempt_set_password(password: 'unvalid', password_confirmation: 'unvalid')      # not correct regex
    @account.attempt_set_password(password: '123', password_confirmation: '123')              # not correct regex, short
    @account.attempt_set_password(password: 'unvalid123', password_confirmation: 'unvalid123')# not correct regex
    @account.attempt_set_password(password: 'UNVALID', password_confirmation: 'UNVALID')      # not correct regex
    @account.attempt_set_password(password: 'uN1', password_confirmation: 'uN1')              # too short

    assert @account.password.eql?('this_IS_123_also_valid'), 'Invalid password set'
    assert @account.password_confirmation.eql?('this_IS_123_also_valid'), 'Invalid Password_confirmation set'
  end

  test 'has_no_password?' do
    assert_not @account.has_no_password?, 'Valid account has password'
    assert @faulty_account_no_pass.has_no_password?, 'Invalid account has no password'
  end

  test 'password_valid?' do
    assert @account.password_valid?, 'Valid account has invalid password'
    assert_not @faulty_account.password_valid?, 'Invalid account has valid password'
  end

  test 'names?' do
    @account.names?
    @faulty_account.names?

    assert @account.errors.empty?, 'Valid names produce errors'
    assert_not @faulty_account.errors.empty?, 'Invalid names do not produce errors'
  end

  test 'confirmed?' do
    assert @account.confirmed?, 'Valid account not confirmed'
    assert_not @faulty_account.confirmed?, 'Invalid account confirmed'
  end
end