class UserAddress < ActiveRecord::Base
  has_one :user

  validates :user_id, :street, :city, :country, :zip, presence: true, blank: false
end
