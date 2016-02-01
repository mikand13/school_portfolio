FactoryGirl.define do
  factory :cart do
    user_id 1
  end

  factory :faulty_cart, class: Cart do
  end
end
