# noinspection RubyResolve
class ConfirmationsController < Devise::ConfirmationsController
  skip_before_filter :authenticate_user!
  respond_to :js

  def create
    self.resource = resource_class.new(resource_params)

    if verify_recaptcha && !resource.email.blank?
      if resource.valid?
        resource.send_confirmation_instructions
      end
      yield resource if block_given?
    else
      resource.valid?
      resource.errors[:recaptcha_error] = t('recaptcha_error')
    end

    if resource.errors.empty?
      if successfully_sent?(resource)
        respond_with({}, location: after_resending_confirmation_instructions_path_for)
      else
        respond_with(resource)
      end
    else
      respond_with(resource)
    end
  end

  def show
    if params[:confirmation_token].present?
      @original_token = params[:confirmation_token]
    elsif params[resource_name].try(:[], :confirmation_token).present?
      @original_token = params[resource_name][:confirmation_token]
    end

    self.resource = resource_class.find_by_confirmation_token Devise.token_generator.
                                                                  digest(self, :confirmation_token, @original_token)

    if resource
      (resource_class.where('email = ?', resource.email).where.not('provider = ?', '').any?) ?
          @names_required = false : @names_required = true

      set_flash_message :notice, :already_registered_on_external_account if is_flashing_format? unless @names_required
    end

    super if resource.nil? or resource.confirmed?
  end

  def confirm
    self.resource = resource_class.find_by_confirmation_token! (params[:confirmation_token])
    resource.assign_attributes(account_params) unless params[resource_name].nil?

    pre_registered_user_account = resource_class.where('email = ?', resource.email).where.not('provider = ?', '').first

    if resource.password_valid? && verify_recaptcha
      self.resource.confirm!

      if pre_registered_user_account
        first_name = pre_registered_user_account.first_name
        last_name = pre_registered_user_account.last_name
        full_name = "#{first_name} #{last_name}"

        resource.update(user_id: pre_registered_user_account.user_id, provider: 'userpass',
                        first_name: first_name, last_name: last_name, full_name: full_name)
      else
        first_name = resource.first_name
        last_name = resource.last_name
        full_name = "#{first_name} #{last_name}"

        user = User.create(first_name: first_name, last_name: last_name, full_name: full_name)
        user.roles << Role.find_by_name(:customer)
        resource.update(user_id: user.id, provider: 'userpass', full_name: full_name)
      end

      set_flash_message :notice, :confirmed

      respond_to do |format|
        format.html { sign_in_and_redirect resource_name, resource }
        format.js { sign_in resource_name, resource }
      end
    else
      resource.errors[:recaptcha] << t('recaptcha_error')
      flash.delete :recaptcha_error

      @names_required = true unless pre_registered_user_account
      render 'devise/confirmations/show'
    end
  end

  protected

  def after_confirmation_path_for
    root_path
  end

  def after_resending_confirmation_instructions_path_for
    root_path
  end

  private

  def resource_params
    params.require(:account).permit(:email, :'g-recaptcha-response')
  end

  def account_params
    params.require(:account).permit(:first_name, :last_name, :confirmation_token, :password, :password_confirmation)
  end
end