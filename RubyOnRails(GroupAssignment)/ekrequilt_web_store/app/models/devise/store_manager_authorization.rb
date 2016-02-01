class StoreManagerAuthorization < ActiveAdmin::AuthorizationAdapter
  def authorized?(action, subject = nil)
    if user.is_a? AdminAccount
      user.role?(:admin) || user.role?(:store_manager)
    end
  end
end