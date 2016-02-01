class AdminAccountsHaveAndBelongToManyRoles < ActiveRecord::Migration
  def change
    create_table :admin_accounts_roles, id: false do |t|
      t.references :role, :admin_account
    end
  end
end
