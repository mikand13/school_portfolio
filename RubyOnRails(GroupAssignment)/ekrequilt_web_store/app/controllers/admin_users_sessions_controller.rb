class AdminUsersSessionsController < Devise::SessionsController
  clear_respond_to
  respond_to :html, constraints: {format: :html}
  respond_to :js, only: :destroy

  def create
    self.resource = warden.authenticate!(auth_options)
    set_flash_message(:notice, :signed_in) if is_flashing_format?
    sign_in(resource_name, resource)
    destroy_guest
    yield resource if block_given?
    respond_with resource, location: after_sign_in_path_for(resource)
  end

  private

  def destroy_guest
    if User.find(session[:guest_user_id])
      user = User.find(session[:guest_user_id])

      if user.orders.empty?
        user.destroy
      end
      session[:guest_user_id] = nil
    end
  end
end