class LineItemsController < ApplicationController

  respond_to :js, only: [:create]

  def create
    product = Product.find(params[:product_id])
    cart = current_user.cart
    line_item = LineItem.find_by_product_and_sellable(product: product, sellable: cart)

    if line_item
      line_item.quantity += 1
      line_item.save
    else
      cart.line_items << LineItem.create(product: product, sellable: cart, quantity: 1)
    end
    respond_with {}
  end
end