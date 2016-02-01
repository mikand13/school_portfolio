class CreateUserAddresses < ActiveRecord::Migration
  def change
    create_table :user_addresses do |t|
      t.belongs_to :user
      t.string :street
      t.string :city
      t.string :state
      t.string :country
      t.string :zip
      t.boolean :default, unique: true, scope: :user

      t.timestamps null: false
    end
  end
end
