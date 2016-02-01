class CreateProductCategories < ActiveRecord::Migration
  def change
    create_table :product_categories do |t|
      t.string :name, unique: true

      t.timestamps null: false
    end
  end
end
