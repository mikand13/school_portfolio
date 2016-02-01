class UsersController < ApplicationController
  before_action :authenticate_account!

  def show
    @orders = @user.orders
  end
end
