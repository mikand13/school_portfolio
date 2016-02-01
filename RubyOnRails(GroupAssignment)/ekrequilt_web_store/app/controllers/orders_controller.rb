class OrdersController < ApplicationController
  before_action :customer?
  load_and_authorize_resource param_method: :set_order, only: [:show, :edit, :destroy]

  def show
  end

  def destroy
    @order.void_authorization

    redirect_to user_path(@order.user_id), status: 303
  end

  def express
    response = EXPRESS_PAYPAL_GATEWAY.setup_authorization(
        Cart.find_by_user_id(current_user.id).price_in_minimum_currency,
        ip:                request.remote_ip,
        return_url:        new_order_url,
        cancel_return_url: products_url,
        currency:          'NOK',
        items: Cart.find_by_user_id(current_user.id).items)
    redirect_to EXPRESS_PAYPAL_GATEWAY.redirect_url_for(response.token)
  end

  def new
    @order = Order.new(express_token: params[:token])
    @order.user_id = current_user.id

    if params[:token]
      @cart = Cart.find_by_user_id(current_user.id)
    end
  end

  def create
    @order = current_user.cart.build_order(order_params)
    @order.ip_address = request.remote_ip
    @order.update(user_id: current_user.id)
    @order.transfer_line_items_from_cart(current_user.cart.line_items)

    respond_to do |format|
      if @order.validate_card && @order.save
        if @order.authorize
          current_user.orders << @order
          current_user.cart.drop_all_line_items

          format.html { redirect_to user_cart_path(current_user.id), notice: t('order_completed') }
          format.json { render :show, status: :created, location: @order }
          format.js { redirect_to user_cart_path(current_user.id), notice: t('order_completed') }
        else
          format.html { redirect_to user_cart_path(current_user.id), notice: t('order_error') }
          format.json { render json: @order.errors, status: :unprocessable_entity }
          format.js { redirect_to user_cart_path(current_user.id), notice: t('order_error') }
        end
      else
        @cart = Cart.find_by_user_id(current_user.id)

        format.html { render :new }
        format.json { render json: @order.errors, status: :unprocessable_entity }
        format.html { render :new }
      end
    end
  end

  private

  def set_order
    @order = Order.find(params[:id])
  end

  def order_params
    params.require(:order).permit(
        :cart_id,
        :express_token, :express_payer_id,
        :ip_address,
        :first_name, :last_name,
        :card_brand, :card_expires_on, :card_number, :card_verification,
        :line_items, :street, :city, :zip, :state, :country)
  end

  def customer?
    unless current_user.role?(:customer) || current_user.role?(:guest)
      raise CanCan::AccessDenied.new(I18n.t 'unauthorized.default', :read, Order)
    end
  end
end
