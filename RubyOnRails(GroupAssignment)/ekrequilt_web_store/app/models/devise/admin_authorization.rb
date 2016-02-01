class AdminAuthorization < ActiveAdmin::AuthorizationAdapter
  def authorized?(action, subject = nil)
    if user.is_a? AdminAccount
      user.role?(:admin)
    end
  end
end