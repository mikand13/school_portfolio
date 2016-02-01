module ActiveAdmin::ViewsHelper #camelized file name
  def get_status(parent, order)
    if order.open?
      parent.status_tag I18n.t('active_admin.users.order_pending')
    elsif order.refunded?
      parent.status_tag I18n.t('active_admin.users.order_refunded'), :warning
    elsif order.voided?
      parent.status_tag I18n.t('active_admin.users.order_voided'), :error
    elsif order.unprocessed?
      parent.status_tag I18n.t('active_admin.users.order_unprocessed'), :error
    else
      parent.status_tag I18n.t('active_admin.users.order_completed'), :ok
    end
  end
end