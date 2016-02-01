require 'test_helper'

class LineItemTest < ActiveSupport::TestCase
  def setup
    @line_item_cart = FactoryGirl.create(:line_item_cart)
    @line_item_order = FactoryGirl.create(:line_item_order)
  end

  def teardown
    @line_item_cart.destroy if @line_item_cart
    @line_item_order.destroy if @line_item_order
  end

  test 'validates_correctly' do
    assert @line_item_cart, 'Valid LineItem not validated correctly'
    assert @line_item_order, 'Unvalid LineItem validated'
    assert !FactoryGirl.build(:faulty_line_item_quantity_zero).valid?, 'Unvalid LineItem validated'
    assert !FactoryGirl.build(:faulty_line_item_sellable_type).valid?, 'Unvalid LineItem validated'
  end

  test 'find_by_product_and_sellable' do
    assert LineItem.find_by_product_and_sellable(product: @line_item_cart.product, sellable: Cart.find(@line_item_cart.sellable_id)), 'LineItem not found'
    assert LineItem.find_by_product_and_sellable(product: @line_item_order.product, sellable: Order.find(@line_item_order.sellable_id)), 'LineItem not found'
  end

  test 'price' do
    assert_equal @line_item_cart.price, @line_item_cart.quantity * Product.find(@line_item_cart.product_id).price, 'Incorrect Price'
  end
end
