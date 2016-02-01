FactoryGirl.define do
  factory :order_cc, class: Order do
    user_id 1
    cart_id 1
    ip_address '127.0.0.1'
    first_name 'test_buyer_first_name'
    last_name 'test_buyer_last_name'
    street 'test_street'
    city 'test_city'
    state 'test_state'
    country 'test_country'
    zip '8378'
    card_number '4024007148673576'
    card_verification '123'
    card_brand 'Visa'
    card_expires_on Time.now
    open true

    before(:create) do |order|
      order.line_items << FactoryGirl.create(:line_item_cart) # bogus line item to pass validation
    end
  end

  factory :order_cc_for_bogus_gateway, class: Order do
    user_id 1
    cart_id 1
    ip_address '127.0.0.1'
    first_name 'test_buyer_first_name'
    last_name 'test_buyer_last_name'
    street 'test_street'
    city 'test_city'
    state 'test_state'
    country 'test_country'
    zip '8378'
    card_number '4024007148673571'
    card_verification '123'
    card_brand 'Visa'
    card_expires_on Time.now
    open true
  end

  factory :order_express, class: Order do
    user_id 1
    cart_id 1
    ip_address '127.0.0.1'
    express_payer_id '124124'
    express_token 'token'
    first_name 'test_buyer_first_name'
    last_name 'test_buyer_last_name'
    open true

    before(:create) do |order|
      order.line_items << FactoryGirl.create(:line_item_cart) # bogus line item to pass validation
    end
  end
end
