class AddColumnsToAccount < ActiveRecord::Migration
  def change
    change_table :accounts do |t|
      t.string :first_name
      t.string :last_name
      t.string :full_name
      t.string :avatar_url
    end
  end
end
