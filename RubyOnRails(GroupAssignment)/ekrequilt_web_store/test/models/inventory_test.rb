require 'test_helper'

class InventoryTest < ActiveSupport::TestCase
  transfer_value = 5

  def setup
    @inventory = FactoryGirl.create(:inventory)
    @faulty_inventory = FactoryGirl.build(:faulty_inventory)
  end

  def teardown
    @inventory.destroy if @inventory
  end

  test 'validates_correctly' do
    assert @inventory, 'Valid Inventory not validated correctly'
    assert_not @faulty_inventory.valid?, 'Unvalid Inventory validated'
  end

  test 'transfer_to_pending_sales' do
    current_available = @inventory.available_quantity
    current_pending = @inventory.pending_sale

    @inventory.transfer_to_pending_sales(transfer_value)

    assert_equal current_pending + transfer_value, @inventory.pending_sale, 'Incorrect increment in pending'
    assert_equal current_available - transfer_value, @inventory.available_quantity, 'Incorrect decrement in available'
  end

  test 'finalize_sale' do
    current_available = @inventory.available_quantity
    current_pending = @inventory.pending_sale

    @inventory.finalize_sale(transfer_value)

    assert_equal @inventory.items_sold, transfer_value, 'Items sold not incremented'
    assert_equal current_pending - transfer_value, @inventory.pending_sale, 'Incorrect decrement in pending'
    assert_equal current_available, @inventory.available_quantity, 'Incorrect available'
  end

  test 'make_available' do
    current_available = @inventory.available_quantity
    current_pending = @inventory.pending_sale

    @inventory.make_available(transfer_value)

    assert_equal current_pending - transfer_value, @inventory.pending_sale, 'Incorrect decrement in pending'
    assert_equal current_available + transfer_value, @inventory.available_quantity, 'Incorrect increment in available'
  end
end
