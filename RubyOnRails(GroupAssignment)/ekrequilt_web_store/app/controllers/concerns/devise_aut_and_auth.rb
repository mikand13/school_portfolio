module DeviseAutAndAuth extend ActiveSupport::Concern
  included do
    before_action :current_user
    after_filter :store_location
  end

  def store_location
    return unless request.get?
    unless request.fullpath =~ /\/accounts/ ||
        request.fullpath =~ /\/admin_accounts/ ||
        request.fullpath.match('/accounts') ||
        request.fullpath.match('/admin_accounts' ||
                                   request.xhr?)
      session[:previous_url] = request.fullpath
    end
  end

  def after_sign_in_path_for(resource)
    if resource && resource.is_a?(AdminAccount)
      if resource.role?(:admin)
        admin_dashboard_url
      elsif resource.role?(:store_manager)
        store_manager_dashboard_url
      end
    elsif resource.nil?
      root_path
    else
      session[:previous_url] || request.env['omniauth.origin'] || stored_location_for(resource) if resource || root_path
    end
  end

  def normal_user_logged_in
    if current_account && current_user.role?(:customer)
      raise CanCan::AccessDenied.new(I18n.t 'unauthorized.normal_account')
    end

    authenticate_admin_account!
  end

  def current_user
    if current_account
      @user = User.find(current_account.user_id)
    elsif defined? session[:guest_user_id] && !session[:guest_user_id].nil?
      unless current_admin_user
        begin
          @user = User.find(session[:guest_user_id])
        rescue ActiveRecord::RecordNotFound
          @user = create_guest_user
        end
      end
    else
      unless current_admin_user
        @user = create_guest_user
      end
    end
  end

  def current_admin_user
    current_admin_account
  end

  def access_denied (exception)
    flash[:alert] = exception.message
    redirect_to root_path
  end

  private

  def create_guest_user
    user = User.create(first_name: "guest #{Time.now}", last_name: "guest #{Time.now}", full_name: "guest #{Time.now}")
    session[:guest_user_id] = user.id

    user
  end
end