class ProductsController < ApplicationController
  before_action :set_product, only: [:show]

  def index
    if params[:search].present?
      @products = Product.search(params[:search]).paginate(:per_page => 10, :page => params[:page])
    elsif params[:category].present?
      @products = Product.where('product_category_id = ?', params[:category]).paginate(page: params[:page], per_page: 10)
    else
      @products = Product.all.paginate(page: params[:page], per_page: 10)
    end
  end

  def show
  end

  private

  def set_product
    @product = Product.find(params[:id])
  end
end
