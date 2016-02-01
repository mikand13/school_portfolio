require 'test_helper'

class AdminAccountTest < ActiveSupport::TestCase
  def setup
    @admin = FactoryGirl.create(:admin_account)
    @faulty_admin = FactoryGirl.build(:faulty_admin_account)
  end

  def teardown
    @admin.destroy if @admin
  end

  test 'validates_correctly' do
    assert @admin, 'Valid AdminAccount not validated correctly'
    assert_not @faulty_admin.valid?, 'Unvalid AdminAccount validated'
  end
end
