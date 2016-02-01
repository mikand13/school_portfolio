ActiveAdmin.register Product do

  form do |f|
    f.inputs do
      f.input :name
      f.input :price
      f.input :image
      f.input :description
      f.inputs for: :inventory do |inventory|
        inventory.input :available_quantity
        inventory.input :pending_sale
      end
      li I18n.t('active_admin.products.created_at', date: l(f.object.created_at)) unless f.object.new_record?
    end

    actions
  end

  remove_filter :line_item
  remove_filter :inventory

end
