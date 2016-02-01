FactoryGirl.define do
  factory :account do
    first_name 'test_user_first_name'
    last_name 'test_user_last_name'
    password 'this_124_IS_valid!'
    password_confirmation 'this_124_IS_valid!'
    email 'test@google.com'
    confirmation_token 'this_is_a_bogus_token'
    confirmed_at Time.now
    confirmation_sent_at Time.now
  end

  factory :faulty_account, class: Account do
    password 'unvalid'
    password_confirmation 'unvalid!'
  end

  factory :faulty_account_no_pass, class: Account do
    email 'test.admin.ekrequilt@ekrequilt.no'
  end
end
