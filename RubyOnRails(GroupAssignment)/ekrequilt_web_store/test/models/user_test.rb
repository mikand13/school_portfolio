require 'test_helper'

class UserTest < ActiveSupport::TestCase
  def setup
    @user = FactoryGirl.create(:user)
    @user_new = FactoryGirl.build(:user)
  end

  def teardown
    @user.destroy if @user
  end

  test 'validates_correctly' do
    @user_new.first_name = ''
    assert_not @user_new.valid?, 'Valid Cart not validated correctly'
  end

  test 'user_creates_cart_after_creation' do
    assert_not_nil @user.cart, 'Cart is nil!'
    assert_equal @user.id, @user.cart.user_id, 'Cart not equal!'
  end

  test 'user_creates_guest_role_after_creation' do
    assert_not_empty @user.roles, 'Roles are empty!'
    assert_equal Role.find_by_name(:guest), @user.roles[0], 'Role not equal!'
  end

  test 'user_does_not_create_cart_on_new' do
    assert_nil @user_new.cart, 'User cart was created on save, should only be created after create!'
  end

  test 'user_does_not_create_guest_role_on_new' do
    assert_empty @user_new.roles, 'Guest role was added to user on save, should only be added after create!'
  end
end
