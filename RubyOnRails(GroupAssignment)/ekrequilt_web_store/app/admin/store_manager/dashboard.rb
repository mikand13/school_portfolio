ActiveAdmin.register_page 'Dashboard', namespace: :store_manager do
  menu priority: 1, label: proc { I18n.t('active_admin.dashboard') }

  content title: proc{ I18n.t('active_admin.dashboard') } do

    columns do
      column do
        panel "#{t('recent')} #{t('activerecord.models.order.other').downcase}" do
          table do
            thead do
              tr do
                th I18n.t('activerecord.attributes.order.open')
                th I18n.t('activerecord.models.user.one')
                th I18n.t('order_cost')
              end
            end
            tbody do
              Order.find_x_most_recent(10).each do |order|
                tr class: cycle('odd', 'even') do
                  td status_tag I18n.t('active_admin.users.order_pending')
                  td link_to order.user.full_name, store_manager_user_path(order.user)
                  td number_to_currency order.price
                end
              end
            end
          end
        end
      end

      column do
        panel "#{t('recent')} #{t('activerecord.models.user.other').downcase}" do
          table do
            thead do
              tr do
                th I18n.t('activerecord.attributes.user.full_name')
                th I18n.t('activerecord.attributes.account.email')
              end
            end
            tbody do
              User.find_x_most_recent(5).each do |user|
                tr class: cycle('odd', 'even') do
                  td link_to user.full_name, store_manager_user_path(user)
                  td user.accounts.first.nil? ? t('unknown') : user.accounts.first.email
                end
              end
            end
          end
        end
      end
    end
  end

end
