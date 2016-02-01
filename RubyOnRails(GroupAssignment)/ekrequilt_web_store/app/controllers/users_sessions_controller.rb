class UsersSessionsController < Devise::SessionsController
  after_filter :set_csrf_headers, only: [:create, :destroy]
  respond_to :js

  def new
    self.resource = resource_class.new(sign_in_params)
    clean_up_passwords(resource)
    yield resource if block_given?
    respond_with(resource, serialize_options(resource))
  end

  def create
    self.resource = warden.authenticate(auth_options)

    if resource && resource.active_for_authentication?
      set_flash_message(:notice, :signed_in)
      sign_in(resource_name, resource)

      destroy_guest
      yield resource if block_given?
    else
      message = warden.message || default || :unauthenticated

      flash.keep(:alert)
      flash[:alert] = I18n.t(:"#{resource_name}.#{message}", scope: 'devise.users_sessions.')
    end

    respond_with resource, location: after_sign_in_path_for(resource)
  end

  def destroy
    signed_out = (Devise.sign_out_all_scopes ? sign_out : sign_out(resource_name))
    set_flash_message :notice, :signed_out if signed_out
    yield if block_given?

    current_user # initialize guest user

    respond_to do |format|
      format.html { redirect_to root_path }
      format.js { render :destroy }
    end
  end

  protected

  def set_csrf_headers
    if request.xhr?
      response.headers['X-CSRF-Token'] = form_authenticity_token
    end
  end

  private

  def destroy_guest
    if User.find(session[:guest_user_id])
      guest = User.find(session[:guest_user_id])
      guest_cart = guest.cart
      current_user.cart.add_line_items(guest_cart)
      guest.destroy
      session[:guest_user_id] = nil
    end
  end
end
