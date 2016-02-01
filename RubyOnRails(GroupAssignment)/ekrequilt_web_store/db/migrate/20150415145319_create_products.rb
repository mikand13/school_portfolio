class CreateProducts < ActiveRecord::Migration
  def change
    create_table :products do |t|
      t.references :product_category
      t.string :name
      t.decimal :price
      t.string :image
      t.text :description

      t.timestamps null: false
    end
  end
end
