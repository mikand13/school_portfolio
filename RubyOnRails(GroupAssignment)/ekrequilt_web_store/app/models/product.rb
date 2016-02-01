class Product < ActiveRecord::Base

  belongs_to :line_item
  belongs_to :product_category
  has_one :inventory

  accepts_nested_attributes_for :inventory, allow_destroy: true
  accepts_nested_attributes_for :product_category, allow_destroy: false

  mount_uploader :image, ProductUploader

  validates :name, :price, :product_category_id, presence: true, blank: false

  def self.in_stock
    Product.joins(:inventory).where('available_quantity > 0')
  end

  def self.out_of_stock
    Product.joins(:inventory).where('available_quantity = 0')
  end

  def self.search(search)
    if search
      where('name LIKE ?', "%#{search}%")
    else
      scoped
    end
  end

end