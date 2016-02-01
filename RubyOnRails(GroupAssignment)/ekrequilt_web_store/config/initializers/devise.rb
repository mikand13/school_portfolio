# Use this hook to configure devise mailer, warden hooks and so forth.
# Many of these configuration options can be set straight in your model.
Devise.setup do |config|
  config.secret_key = ENV['DEVISE_SECRET']

  # ==> Js
  config.navigational_formats = [:html, :js]

  # ==> Mailer Configuration
  config.mailer_sender = 'noreply@ekrequilt.no'

  # ==> ORM configuration
  require 'devise/orm/active_record'

  # ==> Configuration for any authentication mechanism
  config.case_insensitive_keys = [ :email ]
  config.strip_whitespace_keys = [ :email ]
  config.skip_session_storage = [:http_auth]

  # ==> Configuration for :database_authenticatable
  config.stretches = Rails.env.test? ? 1 : 10

  # Setup a pepper to generate the encrypted password.
  config.pepper = ENV['DEVISE_PEPPER']

  # ==> Configuration for :confirmable
  config.reconfirmable = false

  # ==> Configuration for :rememberable
  config.remember_for = 2.weeks
  config.expire_all_remember_me_on_sign_out = true

  # ==> Configuration for :validatable
  config.password_length = 8..128

  # ==> Configuration for :lockable
  config.lock_strategy = :failed_attempts
  config.unlock_strategy = :both
  config.maximum_attempts = 5
  config.unlock_in = 1.hour
  config.last_attempt_warning = true

  # ==> Configuration for :recoverable
  config.reset_password_within = 6.hours

  # ==> Navigation configuration
  config.sign_out_via = :delete

  # ==> OmniAuth
  if Rails.env.production?
    config.omniauth :facebook, ENV['FACEBOOK_ID'], ENV['FACEBOOK_SECRET'], scope: 'email', client_options: {ssl: {ca_path: ENV['SSL_HEROKU_LOCATION']}}
    config.omniauth :gplus, ENV['GOOGLE_ID'], ENV['GOOGLE_SECRET'], scope: 'userinfo.email, userinfo.profile', client_options: {ssl: {ca_path: ENV['SSL_HEROKU_LOCATION']}}
  else
    config.omniauth :facebook, ENV['FACEBOOK_ID'], ENV['FACEBOOK_SECRET'], scope: 'email'
    #,client_options: {ssl: {ca_path: ENV['SSL_HEROKU_LOCATION']}}
    config.omniauth :gplus, ENV['GOOGLE_ID'], ENV['GOOGLE_SECRET'], scope: 'userinfo.email, userinfo.profile'
    #,client_options: {ssl: {ca_path: ENV['SSL_HEROKU_LOCATION']}}
  end

  # ==> Security Extension
  config.password_regex = /(?=.*\d)(?=.*[a-z])(?=.*[A-Z])/
  config.password_archiving_count = 5
  config.deny_old_passwords = true
  config.email_validation = true
end
