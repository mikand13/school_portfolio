class User < ActiveRecord::Base
  include HasRole
  has_many :accounts
  has_many :user_addresses
  has_many :orders
  has_one :cart, dependent: :destroy

  validates :first_name, :last_name, :full_name, presence: true, blank: false

  after_create :create_user_cart
  after_create :create_user_guest_role

  def cart
    Cart.find_by_user_id(id)
  end

  def money_spent
    sum = 0
    orders.each { |order| sum += order.price }
    sum
  end

  private

  def create_user_guest_role
    roles << Role.find_by_name(:guest)
  end

  def create_user_cart
    update(cart: Cart.create(user_id: id))
  end

  def self.find_x_most_recent(amount)
    User.all.order('created_at ASC').limit(amount)
  end
end
