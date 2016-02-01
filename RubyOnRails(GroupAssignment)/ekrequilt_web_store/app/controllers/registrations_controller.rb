class RegistrationsController < Devise::RegistrationsController
  prepend_before_filter :require_no_authentication, only: [:new, :create, :cancel]
  prepend_before_filter :authenticate_scope!, only: [:edit, :update, :destroy]
  after_filter :set_csrf_headers, only: [:destroy]
  respond_to :js

  def new
    build_resource({})
    yield resource if block_given?
    respond_with self.resource
  end

  def create
    build_resource(sign_up_params)

    if verify_recaptcha
      if resource.valid?
        resource.save
        yield resource if block_given?
        if resource.persisted?
          if resource.active_for_authentication?
            set_flash_message :notice, :signed_up if is_flashing_format?
            sign_up(resource_name, resource)
            respond_with resource, location: after_sign_up_path_for(resource)
          else
            set_flash_message :notice, :"signed_up_but_#{resource.inactive_message}" if is_flashing_format?
            expire_data_after_sign_in!
            respond_with resource, location: after_inactive_sign_up_path_for(resource)
          end
        else
          respond_with resource
        end
      end
    else
      resource.valid?
      resource.errors[:recaptcha_error] = t('recaptcha_error')
    end
  end

  def destroy
    resource.destroy
    Devise.sign_out_all_scopes ? sign_out : sign_out(resource_name)
    set_flash_message :notice, :destroyed if is_flashing_format?
    yield resource if block_given?

    respond_to do |format|
      format.html { respond_with_navigational(resource){ redirect_to after_sign_out_path_for(resource_name) } }
      format.js { respond_with(resource) }
    end
  end

  protected

  def set_csrf_headers
    if request.xhr?
      response.headers['X-CSRF-Token'] = form_authenticity_token
    end
  end

  def translation_scope
    'devise.registrations'
  end
end