require 'test_helper'

class ProductTest < ActiveSupport::TestCase
  def setup
    @product = FactoryGirl.create(:product)
  end

  def teardown
    @product.destroy if @product
  end

  test 'validates_correctly' do
    assert_not_nil @product, 'Valid Product not validated correctly'

    product = FactoryGirl.build(:faulty_product)

    assert_not product.valid?, 'Unvalid Product validated'

    product.name = 'test_product'
    product.description = ''

    assert product.valid?, 'Valid Product not validated correctly'

    product.destroy
  end
end
