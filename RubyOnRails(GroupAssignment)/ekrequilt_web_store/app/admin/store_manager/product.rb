ActiveAdmin.register Product, namespace: :store_manager do

  config.sort_order = 'id_asc'
  
  scope :all
  scope :in_stock
  scope :out_of_stock

  controller do

    def scoped_collection
      super.includes :product_category, :inventory
    end

    def new
      @product = Product.new
      @inventory = @product.build_inventory
      @product_category = ProductCategory.first
    end

    def create
      params = permitted_params[:product]

      @product_category = ProductCategory.find(params[:product_category_id])
      params[:product_category] = @product_category

      @product = Product.create(params)

      redirect_to store_manager_product_path(@product)
    end
  end

  permit_params :name, :price, :image, :description, :product_category_id, inventory_attributes: [:id, :available_quantity, :pending_sale, :_destroy]
  actions :all, except: :destroy

  remove_filter :image

  batch_action :newsletter do |ids|
    NewsletterMailer.products_news(Product.find(ids)).deliver_now
    redirect_to collection_path, alert: t('newsletter_sent')
  end

  index do
    selectable_column
    column(:id) { |product| link_to "##{product.id}", store_manager_product_path(product) }
    column :name
    column :product_category, sortable: 'product_categories.name'
    column(:price) { |product| number_to_currency(product.price) }
    column(I18n.t 'activerecord.attributes.inventory.available_quantity') { |product| product.inventory.available_quantity }
    column(I18n.t 'activerecord.attributes.inventory.pending_sale') { |product| product.inventory.pending_sale }
    column(:image) { |product| image_tag product.image_url, width: 92, height: 64 }
    column :created_at
    column :updated_at
    actions
  end

  show do
    attributes_table do
      row(:image) { |product| image_tag product.image_url, width: 92, height: 64 }
      row :id
      row :name
      row(I18n.t('activerecord.attributes.product_category.name'), class: 'row-product_category') { |product| product.product_category.name }
      row(:price) { |product| number_to_currency(product.price) }
      row(I18n.t('activerecord.attributes.inventory.available_quantity'), class: 'row-available_quantity' ) { |product| product.inventory.available_quantity }
      row(I18n.t('activerecord.attributes.inventory.pending_sale'), class: 'row-pending_sale') { |product| product.inventory.pending_sale }
      row(I18n.t('items_sold'), class: 'row-items_sold') { |product| product.inventory.items_sold }
      row :description
      row :created_at
      row :updated_at
    end
  end

  form do |f|
    f.inputs do
      f.input :name
      f.input :price
      f.input :image
      f.input :description
      f.input :product_category, label: I18n.t('activerecord.attributes.product_category.name'),
            as: :select, include_blank: false, collection: ProductCategory.all.map { |pc| ["#{pc.name}", pc.id] }
      f.inputs for: :inventory do |inventory|
        inventory.input :available_quantity
        inventory.input :pending_sale unless f.object.new_record?
      end
      li I18n.t('active_admin.products.created_at', date: l(f.object.created_at)) unless f.object.new_record?
    end

    actions
  end

  remove_filter :line_item
  remove_filter :inventory
end
