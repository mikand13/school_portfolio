require 'test_helper'

class RoleTest < ActiveSupport::TestCase
  def setup
    @role = FactoryGirl.create(:role)
  end

  def teardown
    @role.destroy if @role
  end

  test 'validates_correctly' do
    assert_not_nil @role, 'Valid Role not validated correctly'

    role = FactoryGirl.build(:role)
    role.name = ''

    assert_not role.valid?, 'Unvalid Role validated'

    role.destroy
  end
end
