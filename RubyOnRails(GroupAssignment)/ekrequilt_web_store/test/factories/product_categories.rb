FactoryGirl.define do
  factory :product_category do
    name 'Category #1'
  end

  factory :faulty_product_category, class: ProductCategory do
  end
end
