require 'test_helper'

class UserAddressTest < ActiveSupport::TestCase
  def setup
    @user_address = FactoryGirl.create(:user_address)
  end

  def teardown
    @user_address.destroy if @user_address
  end

  test 'validates_correctly' do
    assert_not_nil @user_address, 'Valid UserAddress not validated correctly'

    user_address = FactoryGirl.build(:user_address)
    user_address.street = ''

    assert_not user_address.valid?, 'Unvalid UserAddress validated'

    user_address.destroy
  end
end
