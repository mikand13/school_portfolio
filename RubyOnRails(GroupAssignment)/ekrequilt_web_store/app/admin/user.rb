ActiveAdmin.register User do

  remove_filter :cart
  remove_filter :subscribed_newsletter
  remove_filter :accounts
  remove_filter :user_addresses

end
