require 'test_helper'

class OrderTransactionTest < ActiveSupport::TestCase
  def setup
    @order_transaction = FactoryGirl.create(:order_transaction)
    @faulty_order_transaction = FactoryGirl.build(:faulty_order_transaction)
  end

  def teardown
    @order_transaction.destroy if @order_transaction
  end

  test 'validates_correctly' do
    assert @order_transaction, 'Valid OrderTransaction not validated correctly'
    assert_not @faulty_order_transaction.valid?, 'Unvalid OrderTransaction validated'
  end
end
