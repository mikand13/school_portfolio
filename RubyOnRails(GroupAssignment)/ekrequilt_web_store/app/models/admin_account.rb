class AdminAccount < ActiveRecord::Base
  include HasRole
  devise :database_authenticatable, :trackable

  validates :email, :encrypted_password, presence: true, allow_blank: false
end
