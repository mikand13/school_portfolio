require 'test_helper'

class CartTest < ActiveSupport::TestCase
  def setup
    @cart = FactoryGirl.create(:cart)
    @guest_cart = FactoryGirl.create(:cart)
    @faulty_cart = FactoryGirl.build(:faulty_cart)
  end

  def teardown
    @cart.destroy if @cart
    @guest_cart.destroy if @guest_cart
  end

  test 'validates_correctly' do
    assert @cart, 'Valid Cart not validated correctly'
    assert_not @faulty_cart.valid?, 'Unvalid Cart validated'
  end

  test 'add_line_items' do
    @guest_cart.line_items << FactoryGirl.create(:line_item_cart)
    @guest_cart.line_items << FactoryGirl.create(:line_item_cart)

    assert @cart.line_items.size == 0, 'LineItem size not correct'
    assert @guest_cart.line_items.size == 2, 'LineItem size not correct'

    @cart.add_line_items(@guest_cart)

    assert @cart.line_items.size == 2, 'LineItem size not correct'
    assert @guest_cart.line_items.size == 0, 'LineItem size not correct'

    @cart.drop_all_line_items
  end

  test 'line_items_count' do
    @cart.line_items << FactoryGirl.create(:line_item_cart)
    assert @cart.line_items_count == 10, 'LineItem count is incorrect'
  end

  test 'drop_all_line_items' do
    @cart.line_items << FactoryGirl.create(:line_item_cart)
    @cart.line_items << FactoryGirl.create(:line_item_cart)

    assert @cart.line_items.size > 0, 'LineItem size not correct'

    @cart.drop_all_line_items

    assert @cart.line_items.size == 0, 'LineItem size not correct'
  end
end
