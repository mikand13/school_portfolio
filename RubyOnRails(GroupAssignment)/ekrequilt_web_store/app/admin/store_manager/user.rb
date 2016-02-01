ActiveAdmin.register User, namespace: :store_manager do

  config.sort_order = 'id_asc'

  actions :index, :show
  remove_filter :user_addresses
  remove_filter :cart
  remove_filter :orders
  remove_filter :accounts

  show do
    panel I18n.t('active_admin.users.order_history') do
      table do
        thead do
          tr do
            th t('active_admin.users.order_number')
            th t('active_admin.users.order_status')
            th t('active_admin.users.order_sum')
            th t('active_admin.users.order_date')
          end
        end
        tbody do
          user.orders.each do |order|
            tr do
              th link_to "##{order.id}", store_manager_order_path(order)
              th get_status(self, order)
              th number_to_currency(order.price)
              th l(order.created_at)
            end
          end
        end
      end
    end
  end

  sidebar I18n.t('active_admin.users.customer_details'), only: :show do
    dl do
      dt t('activerecord.attributes.account.email')
      if user.accounts.first
        dd user.accounts.first.email
      else
        dd t('guest_email')
      end
      dt t('activerecord.attributes.user.full_name')
      dd user.full_name
      dt t('activerecord.attributes.user.created_at')
      dd l(user.created_at, format: :long)
    end
  end

  sidebar I18n.t('active_admin.users.order_history'), only: :show do
    dl do
      dt t('active_admin.users.order_count')
      dd user.orders.size
      dt t('active_admin.users.order_sum')
      dd number_to_currency(user.money_spent)
    end
  end

end
