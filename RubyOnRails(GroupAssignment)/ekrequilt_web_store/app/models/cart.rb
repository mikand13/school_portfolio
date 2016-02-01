class Cart < ActiveRecord::Base
  include Sellable
  belongs_to :user

  has_one :order, dependent: :destroy

  validates :user_id, presence: true

  def add_line_items guest_cart
    guest_cart.line_items.each do |item|
      increment_or_add_line_item(item)
    end

    guest_cart.drop_all_line_items
  end

  def line_items_count
    items = 0
    line_items.each { |item| items += item.quantity }
    items
  end

  def drop_all_line_items
    line_items.destroy_all
  end

  private

  def increment_or_add_line_item(item)
    line_item = LineItem.find_by_product_and_sellable(product: item.product, sellable: user.cart)

    if line_item
      line_item.quantity += item.quantity
      line_item.save
    else
      line_items << LineItem.create(product: item.product, sellable: user.cart, quantity: item.quantity)
    end
  end
end
