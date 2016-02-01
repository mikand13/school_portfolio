ActiveAdmin.register Order, namespace: :store_manager do

  config.sort_order = 'status_asc'

  actions :index, :show

  member_action :authorize, method: :put do
    @order = Order.find(params[:id])
    unless @order.authorize
      flash[:notice] = t('error_processing_order')
    end
    redirect_to store_manager_order_path @order
  end

  member_action :capture, method: :put do
    @order = Order.find(params[:id])
    unless @order.capture
      flash[:notice] = t('error_processing_order')
    end
    redirect_to store_manager_order_path @order
  end

  member_action :refund, method: :put do
    @order = Order.find(params[:id])
    unless @order.refund
      flash[:notice] = t('error_processing_order')
    end
    redirect_to store_manager_order_path @order
  end

  member_action :void, method: :put do
    @order = Order.find(params[:id])
    unless @order.void_authorization
      flash[:notice] = t('error_processing_order')
    end
    redirect_to store_manager_order_path @order
  end

  scope :all
  scope :completed
  scope :pending
  scope :refunded
  scope :cancelled

  remove_filter :ip_address
  remove_filter :express_token
  remove_filter :street
  remove_filter :state
  remove_filter :country
  remove_filter :card_brand
  remove_filter :card_expires_on
  remove_filter :open
  remove_filter :line_items
  remove_filter :order_transactions

  index do
    column(:id) { |order| link_to "##{order.id}", store_manager_order_path(order) }
    column(:status) { |order| get_status(self, order) }
    column :purchased_at
    column(I18n.t('activerecord.models.user.one'), class: 'col-user') { |order| link_to order.user.full_name, store_manager_user_path(order.user) }
    column(I18n.t('activerecord.attributes.account.email'), class: 'col-email') { |order| order.user.accounts.first.nil? ? I18n.t('unknown') : order.user.accounts.first.email }
    column(I18n.t('order_cost')) { |order| number_to_currency(order.price) }
  end

  show do
    panel I18n.t('activerecord.models.order.other') do
      h1 get_status(self, order)

      table do
        thead do
          tr do
            th t('activerecord.models.product.one')
            th t('quantity')
            th t('activerecord.attributes.product.price')
            th t('activerecord.models.user')
          end
        end
        tbody do
          order.line_items.each do |line_item|
            tr class: cycle('odd', 'even') do
              td link_to line_item.product.name, store_manager_product_path(line_item.product)
              td line_item.quantity
              td number_to_currency(line_item.price)
            end
          end
          tr do
            td
            td "Total:", style: 'text-align: right;'
            td number_to_currency(order.price), class: 'sum-price'
          end
        end
      end
      if order.open?
        a I18n.t('confirm_order'), href: capture_store_manager_order_path(order), 'data-method': :put, style: 'text-align: right;', class: 'button'
        a I18n.t('cancel_order'), href: void_store_manager_order_path(order), 'data-method': :put, style: 'text-align: right;', class: 'button'
      else
        if order.captured?
          a I18n.t('refund_order'), href: refund_store_manager_order_path(order), 'data-method': :put, style: 'text-align: right;', class: 'button'
        elsif order.unprocessed?
          a I18n.t('authorize_order'), href: authorize_store_manager_order_path(order), 'data-method': :put, style: 'text-align: right;', class: 'button'
        end
      end
    end
  end

  sidebar I18n.t('active_admin.users.customer_details'), only: :show do
    dl do
      dt t('activerecord.attributes.account.email')
      if order.user.accounts.first
        dd order.user.accounts.first.email
      else
        dd t('guest_email')
      end
      dt t('activerecord.attributes.user.full_name')
      dd order.user.full_name
    end
  end

  sidebar 'Mottakeradresse', only: :show do
    dl do
      dt t('address')
      dd order.street
      dd "#{order.zip} #{order.city}"
    end
  end

end
