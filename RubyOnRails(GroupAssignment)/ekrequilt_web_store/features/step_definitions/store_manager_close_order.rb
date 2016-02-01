And(/^that I am on the show page of a order that is open$/) do
  @user = User.find(3)
  @order = @user.orders.where(open: true).first
  visit store_manager_order_path(@order)
  assert page.has_content? I18n.t('active_admin.users.order_pending').titleize
end

When(/^I confirm the order$/) do
  click_link I18n.t('confirm_order')
end

Then(/^the order should be closed$/) do
  @order.reload
  assert page.has_content? I18n.t('active_admin.orders.completed').titleize
  refute @order.open?
end