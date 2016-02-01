class Inventory < ActiveRecord::Base
  belongs_to :product

  self.primary_key = :product_id

  validates :available_quantity, :pending_sale, presence: true
  validates :available_quantity, numericality: { greater_than_or_equal_to: 0 }
  validates :pending_sale, numericality: { greater_than_or_equal_to: 0 }

  def transfer_to_pending_sales (amount)
    self.available_quantity -= amount
    self.pending_sale += amount
    self.save
  end

  def finalize_sale (amount)
    self.pending_sale -= amount
    self.items_sold += amount
    self.save
  end

  def make_available (amount)
    self.pending_sale -= amount
    self.available_quantity += amount
    self.save
  end
end
