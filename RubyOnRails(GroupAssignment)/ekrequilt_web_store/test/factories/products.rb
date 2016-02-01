FactoryGirl.define do
  factory :product do
    product_category_id 1
    name 'test_product'
    price 10000
    image 'bogus_url'
    description 'test_product_description'
  end

  factory :faulty_product, class: Product do
    product_category_id 1
    price 10000
    image 'bogus_url'
    description 'test_product_description'
  end
end
