# This file should contain all the record creation needed to seed the database with its default values.

def create_product_category(attr = {})
  ProductCategory.create(name: attr[:name])
end

def create_product(attr = {})
  product = Product.create(name: attr[:name], product_category: attr[:product_category], price: attr[:price], description: attr[:description])
  product[:image] = attr[:image_url]
  Inventory.create(product: product, available_quantity: 20, pending_sale: 0)
  product.save
end

def create_role(attr = {})
  Role.create(name: attr[:name])
end

def create_site_user(attr = {})
  admin_account = AdminAccount.create(email: attr[:email], password: attr[:password], password_confirmation: attr[:password_confirmation])
  admin_account.roles << Role.find_by_name(attr[:role])
end

def create_user(attr = {})
  user = User.create(first_name: attr[:first_name], last_name: attr[:last_name], full_name: attr[:full_name], subscribed_newsletter: attr[:subscribed_newsletter])
  account = Account.new(user_id: user.id, email: attr[:email], password: attr[:password], password_confirmation: attr[:password_confirmation],
                 first_name: user.first_name, last_name: user.last_name, full_name: user.full_name)
  account.confirm!
  account.save(validate: false)
  user.roles << Role.find_by_name(attr[:role])
end

def create_order(attr = {})
  user = attr[:user]

  order = Order.new(
      user:               user,
      cart_id:            user.cart.id,
      ip_address:         '127.0.0.1',
      first_name:         user.first_name,
      last_name:          user.last_name,
      street:             'Veien 30 A',
      city:               'Oslo',
      state:              'Oslo',
      country:            'Norge',
      zip:                '0571',
      open:               attr[:open] ? true : false
  )

  if Rails.env.test?
    cc_number = '4024007148673571'
  else
    cc_number = '4032036508055587'
  end

  order.card_expires_on = Time.now

  credit_card = ActiveMerchant::Billing::CreditCard.new(
      brand:              'visa',
      number:             cc_number,
      verification_value: '123',
      month:              order.card_expires_on.month,
      year:               order.card_expires_on.year + 1,
      first_name:         user.first_name,
      last_name:          user.last_name
  )

  order.instance_variable_set(:@credit_card, credit_card)

  class << order
    def validate_card
      true
    end
  end

  populate_order order

  order.save

  order.authorize

  order
end

def populate_order(order)
  (0..3).each do
    offset = rand(Product.count)

    if offset == 0
      offset = 1
    end

    order.line_items << LineItem.create(product: Product.find(offset), sellable: order, quantity: rand(10) + 1)
  end
end

create_product_category name: 'Løpere'
create_product_category name: 'Duker'
create_product_category name: 'Vesker'
create_product_category name: 'Veggbilder'
create_product_category name: 'Brikker'
create_product_category name: 'Toalettmapper'

create_product name: 'Sekskantet brikke i julestoff', product_category: ProductCategory.find(5), price: 10, image_url: 'http://res.cloudinary.com/mikrondok/image/upload/v1429437332/Brikke_6_kantet_i_julefarger_nfajm9.jpg', description: 'En fin stoffbit'
create_product name: 'Bølgeløper i rosa hvit og sort', product_category: ProductCategory.find(1), price: 10, image_url: 'https://res-2.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437336/L%C3%B8per_9_graders_linial_Rosa_Hvit_Sort_sjgeej.jpg', description: 'En annen fin stoffbit'
create_product name: 'Sekskantet duk offwhite lilla', product_category: ProductCategory.find(2), price: 10, image_url: 'https://res-4.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437340/L%C3%B8per_Advent_Avlang_6_kantet_Beige_Lilla_ggintc.jpg', description: 'En tredje fin stoffbit'
create_product name: 'Hurtigløper med solsikker', product_category: ProductCategory.find(1), price: 10, image_url: 'https://res-3.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437356/L%C3%B8per_Avlang_6_kantet_Gul_Gr%C3%B8nn_ohwtmo.jpg', description: 'En fjerde fin stoffbit'
create_product name: 'Sekskantet duk i orange med brune blomster', product_category: ProductCategory.find(2), price: 10, image_url: 'https://res-2.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437359/L%C3%B8per_Avlang_6_kantet_Gul_Orange_R%C3%B8d_ojdlmt.jpg', description: 'En femte fin stoffbit'
create_product name: 'Lang løper med hjerter', product_category: ProductCategory.find(1), price: 10, image_url: 'https://res-1.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437363/L%C3%B8per_Beige_Bl%C3%A5_med_hjerter_jnun6j.jpg', description: 'En sjette fin stoffbit'
create_product name: 'Løper med burgunder firkanter', product_category: ProductCategory.find(1), price: 10, image_url: 'https://res-4.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437365/L%C3%B8per_Burgunder_Krem_iher8v.jpg', description: 'En sjuende fin stoffbit'
create_product name: 'Løper grønn med gule blomster', product_category: ProductCategory.find(1), price: 10, image_url: 'https://res-5.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437368/L%C3%B8per_Gr%C3%B8nn_Hvit_med_gule_blomster_yw8ooh.jpg', description: 'En åttende fin stoffbit'
create_product name: 'Løper med blomster', product_category: ProductCategory.find(1), price: 10, image_url: 'https://res-5.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437371/L%C3%B8per_Gr%C3%B8nn_med_blomster_scdn6q.jpg', description: 'En niende fin stoffbit'
create_product name: 'Løper blå grønne firkanter', product_category: ProductCategory.find(1), price: 10, image_url: 'https://res-4.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437375/L%C3%B8per_Gr%C3%B8nn_Turkis_Bl%C3%A5_g3kbon.jpg', description: 'En tiende fin stoffbit'
create_product name: 'Løper gul og orange med blomster', product_category: ProductCategory.find(1), price: 10, image_url: 'https://res-4.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437377/L%C3%B8per_Gulorange_Hvit_osp24h.jpg', description: 'En ellevte fin stoffbit'
create_product name: 'Løper med adventslys', product_category: ProductCategory.find(1), price: 10, image_url: 'https://res-4.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437380/L%C3%B8per_Jul_Sort_med_4_lys_yv3gxy.jpg', description: 'En tolvte fin stoffbit'
create_product name: 'Løper med sorte blomster', product_category: ProductCategory.find(1), price: 10, image_url: 'https://res-5.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437383/L%C3%B8per_med_sorte_blomster_grtaob.jpg', description: 'En trettende fin stoffbit'
create_product name: 'Duk med jordbærmotiv', product_category: ProductCategory.find(2), price: 10, image_url: 'https://res-4.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437385/L%C3%B8per_Okergul_med_jordb%C3%A6r_i0brcj.jpg', description: 'En fjortende fin stoffbit'
create_product name: 'Duk i rutessfasong', product_category: ProductCategory.find(2), price: 10, image_url: 'https://res-5.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437389/L%C3%B8per_Ruteress_Gr%C3%B8nn_Beige_jq8jbc.jpg', description: 'En femtende fin stoffbit'
create_product name: 'Løper i høstfarger', product_category: ProductCategory.find(1), price: 10, image_url: 'https://res-2.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437391/L%C3%B8per_Sort_med_to_felt_med_h%C3%B8stfarger_qplxf5.jpg', description: 'En sekstende fin stoffbit'
create_product name: 'Spisebrikke med frukt og grønt', product_category: ProductCategory.find(5), price: 10, image_url: 'https://res-1.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437393/Spisebrikker_med_frukter_zvhxln.jpg', description: 'En syttende fin stoffbit'
create_product name: 'Toalettmappe i flanell', product_category: ProductCategory.find(6), price: 10, image_url: 'https://res-4.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437395/Toalettmappe_Lys_bl%C3%A5_flanell_iljyhe.jpg', description: 'En attende fin stoffbit'
create_product name: 'Prikken over i`en', product_category: ProductCategory.find(4), price: 10, image_url: 'https://res-3.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437398/Veggbilde_Prikken_over_ien_Orange_Sort_ovojku.jpg', description: 'En nittende fin stoffbit'
create_product name: 'Sort og hvit veske med lommer', product_category: ProductCategory.find(3), price: 20, image_url: 'https://res-3.cloudinary.com/mikrondok/image/upload/t_media_lib_thumb/v1429437400/Veske_Sort_Hvit_ii0t1n.jpg', description: 'En tyvende fin stoffbit'

create_role name: :admin
create_role name: :store_manager
create_role name: :customer
create_role name: :guest

create_site_user first_name: 'admin', last_name: 'admin', full_name: 'admin', email: 'admin@ekrequilt.no', password: 'dev', password_confirmation: 'dev', role: :admin
create_site_user first_name: 'store_manager', last_name: 'store_manager', full_name: 'store_manager', email: 'store_manager@ekrequilt.no', password: 'dev', password_confirmation: 'dev', role: :store_manager
create_user first_name: 'Anders', last_name: 'Mikkelsen', full_name: 'Anders Mikkelsen', email: 'mikand13@ekrequilt.no', password: 'dev', password_confirmation: 'dev', role: :customer, subscribed_newsletter: true
create_user first_name: 'Espen', last_name: 'Rønning', full_name: 'Espen Rønning', email: 'ronesp13@ekrequilt.no', password: 'dev', password_confirmation: 'dev', role: :customer, subscribed_newsletter: true
create_user first_name: 'Pia', last_name: 'Dokken', full_name: 'Pia Dokken', email: 'strpia13@ekrequilt.no', password: 'dev', password_confirmation: 'dev', role: :customer, subscribed_newsletter: false

create_order(user: User.find(1)).capture
create_order user: User.find(2)
create_order(user: User.find(2)).capture
create_order user: User.find(1)
create_order(user: User.find(3)).void_authorization
create_order user: User.find(3)
create_order(user: User.find(2)).void_authorization
create_order user: User.find(1)

refunded_order = create_order(user: User.find(3))
refunded_order.capture
refunded_order.refund