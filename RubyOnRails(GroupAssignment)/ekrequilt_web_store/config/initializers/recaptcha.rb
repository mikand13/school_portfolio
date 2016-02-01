Recaptcha.configure do |config|
  config.use_ssl_by_default = true
  config.public_key  = ENV['RECAPTCHA_PUBLIC_KEY']
  config.private_key = ENV['RECAPTCHA_PRIVATE_KEY']
end