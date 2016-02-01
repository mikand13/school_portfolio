# noinspection RubyResolve,RubyResolve
class ApplicationController < ActionController::Base
  # Prevent CSRF attacks by raising an exception.
  # For APIs, you may want to use :null_session instead.
  protect_from_forgery with: :exception

  include DeviseAutAndAuth

  rescue_from(CanCan::AccessDenied) { |exception|
    flash[:alert] = exception.message
    redirect_to root_path
  }

  rescue_from(ActionController::UnknownFormat) {
    flash[:alert] = t('uri_hack')
    redirect_to root_path
  }
end
