FactoryGirl.define do
  factory :line_item_cart, class: LineItem do
    product_id 1
    quantity 10
    association :sellable, factory: :cart
  end

  factory :line_item_order, class: LineItem do
    product_id 2
    quantity 20
    association :sellable, factory: :order_cc
  end

  factory :faulty_line_item_sellable_type, class: LineItem do
    product_id 3
    quantity 10
    association :sellable, factory: :product
  end

  factory :faulty_line_item_quantity_zero, class: LineItem do
    product_id 4
    quantity 0
    association :sellable, factory: :cart
  end
end
