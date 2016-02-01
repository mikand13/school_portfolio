class CartsController < ApplicationController
  before_action :customer?
  before_action :set_cart

  def show
  end

  def destroy
    @cart.drop_all_line_items

    render :show
  end

  private

  def customer?
    unless current_user.role?(:customer) || current_user.role?(:guest)
      raise CanCan::AccessDenied.new(I18n.t 'unauthorized.default', :read, Cart)
    end
  end

  def set_cart
    @cart = User.find(params[:id]).cart
    authorize! :show, @cart

    @line_items = @cart.line_items
  end
end