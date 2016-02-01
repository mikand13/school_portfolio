class CartsHaveAndBelongToManyLineItems < ActiveRecord::Migration
  def change
    create_table :carts_line_items, id: false do |t|
      t.references :cart, :line_item
    end
  end
end
