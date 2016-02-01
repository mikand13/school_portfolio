class CreateLineItems < ActiveRecord::Migration
  def change
    create_table :line_items do |t|
      t.references :product
      t.integer :quantity
      t.references :sellable, polymorphic: true

      t.timestamps null: false
    end
  end
end
