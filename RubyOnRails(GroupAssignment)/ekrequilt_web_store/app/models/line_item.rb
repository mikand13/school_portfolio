class LineItem < ActiveRecord::Base
  belongs_to :sellable, polymorphic: true

  belongs_to :product

  validates :product, :quantity, :sellable, presence: true
  validates :sellable_type, inclusion: { in: %w(Cart Order)} # hardcoded for all relevant sellables
  validates :quantity, numericality: { greater_than: 0 }

  def self.find_by_product_and_sellable(params)
    LineItem.where('product_id = ?', params[:product].id)
        .where('sellable_id = ?', params[:sellable].id)
        .where('sellable_type = ?', params[:sellable].class.to_s).first
  end

  def price
    quantity * product.price
  end
end
