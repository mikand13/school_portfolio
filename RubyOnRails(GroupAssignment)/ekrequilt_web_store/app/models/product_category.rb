class ProductCategory < ActiveRecord::Base
  validates :name, presence: true, blank: false
end
