require 'test_helper'

class OrderTest < ActiveSupport::TestCase
  def setup
    @order_legit_cc = FactoryGirl.create(:order_cc)
    @order_cc = FactoryGirl.build(:order_cc_for_bogus_gateway)
    @order_cc.line_items << FactoryGirl.create(:line_item_cart) # bogus line item to pass validation
    @order_cc.save(validate: false) # hack needed to get an "illegitamate" cc number to work with bogus gateway,
                                    # must end with 1 for success
  end

  def teardown
    @order_cc.destroy if @order_cc
  end

  test 'authorize' do
    assert @order_cc.authorize, 'Legit order not authorized correctly'
    assert @order_cc.authorized?
    assert @order_cc.open, 'Authorized order not open'
  end

  test 'capture' do
    @order_cc.authorize
    assert @order_cc.capture, 'Authorized order not captured correctly'
    assert @order_cc.captured?
    assert_not @order_cc.open, 'Paid order still open'
  end

  test 'refund' do
    @order_cc.authorize
    @order_cc.capture
    assert @order_cc.refund, 'Captured order not refunded correctly'
    assert @order_cc.refunded?
    assert_not @order_cc.capture, 'Refunded order capturable'
    assert_not @order_cc.open, 'Refunded order still open'
  end

  test 'void_authorization' do
    @order_cc.authorize
    assert @order_cc.void_authorization, 'Authorized order not voided correctly'
    assert @order_cc.voided?
    assert_not @order_cc.capture, 'Voided order capturable'
    assert_not @order_cc.open, 'Voided order still open'
  end

  test 'validate_card' do
    @order_legit_cc.validate_card
    assert @order_legit_cc.errors.empty?, 'Valid card not validated'
  end

  test 'transfer_line_items_from_cart' do
    @guest_cart = FactoryGirl.create(:cart)
    @guest_cart.line_items << FactoryGirl.create(:line_item_cart)
    @guest_cart.line_items << FactoryGirl.create(:line_item_cart)

    assert @order_legit_cc.line_items.size == 1, 'Incorrect size of line_items' # one comes with factory

    @order_legit_cc.transfer_line_items_from_cart(@guest_cart.line_items)

    assert @order_legit_cc.line_items.size == 3, 'Incorrect size of line_items'

    @guest_cart.drop_all_line_items

    @order_legit_cc.line_items.each { |item| item.destroy }
  end

  test 'open?' do
    assert @order_cc.open?, 'Open showing closed, or opposite'
  end

  test 'display_name' do
    assert_equal "#{I18n.t('activerecord.models.order.one')} ##{@order_cc.id} - #{ActionController::Base.helpers.number_to_currency(@order_cc.price)}",
                 @order_cc.display_name,
                 'Incorrect display name'
  end

  test 'self.completed' do
    assert_equal Order.completed, Order.where(open: false).order('created_at ASC'), 'Incorrect SQL'
  end

  test 'self.pending' do
    assert_equal Order.pending, Order.where(open: true).order('created_at ASC'), 'Incorrect SQL'
  end

  test 'self.find_x_most_recent' do
    amount = 2

    assert_equal Order.find_x_most_recent(amount), Order.where(open: true).order('created_at ASC').limit(amount),
                 'Incorrect SQL'
  end
end
