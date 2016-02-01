FactoryGirl.define do
  factory :user_address do
    user_id 1
    street 'test_street'
    city 'test_city'
    state 'test_state'
    country 'test_country'
    zip '04675'
  end
end
