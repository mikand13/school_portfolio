class PasswordsController < Devise::PasswordsController
  respond_to :js

  def update
    self.resource = resource_class.reset_password_by_token(resource_params)
    yield resource if block_given?

    resource.password_valid?

    if resource.errors.empty?
      resource.unlock_access! if unlockable?(resource)
      flash_message = resource.active_for_authentication? ? :updated : :updated_not_active
      set_flash_message(:notice, flash_message)
      sign_in(resource_name, resource)
      respond_with resource, location: after_resetting_password_path_for(resource)
    else
      respond_with resource
    end
  end

  protected

  def after_sending_reset_password_instructions_path_for(resource_name)
    root_path
  end
end