module Sellable extend ActiveSupport::Concern
  included do
    has_many :line_items, as: :sellable
  end

  def items
    line_items.map do |line_item|
      {
          name: line_item.product.name,
          number: line_item.product.id,
          quantity: line_item.quantity,
          description: line_item.product.description,
          amount: line_item.product.price.to_i * 100
      }
    end
  end

  def price_in_minimum_currency
    price * 100
  end


  def price
    sum = 0
    line_items.each { |l| sum += l.price }
    sum
  end
end