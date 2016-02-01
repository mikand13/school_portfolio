class UnlocksController < Devise::UnlocksController
  respond_to :js

  protected

  def after_unlock_path_for(resource)
    respond_to do |format|
      format.all { root_path }
    end
  end
end