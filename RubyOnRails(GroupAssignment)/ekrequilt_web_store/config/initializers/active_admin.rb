ActiveAdmin.setup do |config|
  # == Controller Filters
  config.before_filter :store_location

  # == Default Site Title
  config.site_title = 'Ekrequilt'

  # == Admin Comments
  config.comments = false

  config.namespace :admin do |admin|
    # I18n bugs out
    # admin.site_title = I18n.t('active_admin.admin')
    admin.site_title_link = '/'
    admin.authorization_adapter = AdminAuthorization
  end

  config.namespace :store_manager do |store_manager|
    # I18n bugs out
    # store_manager.site_title = I18n.t('active_admin.store_manager')
    store_manager.site_title_link = '/'
    store_manager.authorization_adapter = StoreManagerAuthorization
  end

  # == Logo
  # Set an optional image to be displayed for the header
  # instead of a string (overrides :site_title)
  #
  # Note: Aim for an image that's 21px high so it fits in the header.
  #
  # config.site_title_image = "logo.png"

  # == User Authentication
  config.authentication_method = :normal_user_logged_in

  # == User Authorization
  config.on_unauthorized_access = :access_denied

  # == Current User
  config.current_user_method = :current_admin_user

  # == Logging Out
  config.logout_link_path = :destroy_admin_account_session_path
  config.logout_link_method = :delete

  # == Root
  # Default:
  # config.root_to = 'dashboard#index'

  # == Batch Actions
  config.batch_actions = true

  # == Setting a Favicon
  #
  # config.favicon = 'favicon.ico'
end
