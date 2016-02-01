FactoryGirl.define do
  params_hash = {one: 'test_info'}
  faulty_params_hash = {}

  factory :order_transaction do
    order_id 1
    action 'authorize'
    amount 1000
    success true
    authorization nil
    message 'test_order_transaction_message'
    params params_hash
  end

  factory :faulty_order_transaction, class: OrderTransaction do
    order_id 1
    action 'authorize'
    amount 1000
    success true
    authorization nil
    message 'test_order_transaction_message'
    params faulty_params_hash
  end
end
