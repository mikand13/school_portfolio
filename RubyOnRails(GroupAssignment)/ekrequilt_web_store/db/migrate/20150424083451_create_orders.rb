class CreateOrders < ActiveRecord::Migration
  def change
    create_table :orders do |t|
      t.integer :user_id
      t.integer :cart_id
      t.string :ip_address
      t.string :express_payer_id
      t.string :express_token
      t.string :first_name
      t.string :last_name
      t.string :street
      t.string :city
      t.string :state
      t.string :country
      t.string :zip
      t.string :card_brand
      t.date :card_expires_on
      t.boolean :open
      t.column :status, :integer, default: 0
      t.date :purchased_at
      t.date :closed_at

      t.timestamps null: false
    end
  end
end
