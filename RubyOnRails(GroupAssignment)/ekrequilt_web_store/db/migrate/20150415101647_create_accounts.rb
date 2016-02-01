class CreateAccounts < ActiveRecord::Migration
  def change
    create_table :accounts do |t|
      t.belongs_to :user, index: true, unique: false
      t.string :provider
      t.string :uid

      t.timestamps null: true
    end
  end
end
