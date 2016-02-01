FactoryGirl.define do
  factory :inventory do
    product_id factory: :product
    available_quantity 100
    pending_sale 50
    items_sold 0
  end

  factory :faulty_inventory, class: Inventory do
    product_id factory: :product
    available_quantity -5
    pending_sale 50
    items_sold 0
  end
end
