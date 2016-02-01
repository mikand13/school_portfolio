class CreateInventories < ActiveRecord::Migration
  def change
    create_table :inventories, id: false, primary_key: :product_id do |t|
      t.references :product
      t.integer :available_quantity
      t.integer :pending_sale, default: 0
      t.integer :items_sold, default: 0

      t.timestamps null: false
    end
    add_index :inventories, :product_id, unique: true
  end
end