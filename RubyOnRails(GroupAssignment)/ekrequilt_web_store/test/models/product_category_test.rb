require 'test_helper'

class ProductCategoryTest < ActiveSupport::TestCase
  def setup
    @product_category = FactoryGirl.create(:product_category)
    @faulty_product_category = FactoryGirl.build(:faulty_product_category)
  end

  def teardown
    @product_category.destroy if @product
  end

  test 'validates_correctly' do
    assert @product_category, 'Valid ProductCategory not validated correctly'
    assert_not @faulty_product_category.valid?, 'Invalid ProductCategory validated correctly'
  end
end
