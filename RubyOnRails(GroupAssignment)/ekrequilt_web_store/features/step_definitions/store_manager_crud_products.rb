And(/^that I am on the product creation page$/) do
  visit new_store_manager_product_path
end

When(/^I fill in the product information$/) do
  fill_in 'product_name', with: 'Vannlilje'
  fill_in 'product_price', with: '13.37'
  fill_in 'product_description', with: 'Et vakkert veggbilde med en vannlilje'
  select 'Veggbilder', from: 'product_product_category_id'
  fill_in 'product_inventory_attributes_available_quantity', with: '7'
  @product = Product.last
  click_button "Create Produkt"
end

Then(/^I should see the information for the created product$/) do
  product = Product.last
  refute_equal @product, product
  assert current_url, store_manager_product_url(product)

  within('h2#page_title') { assert page.has_content? 'Vannlilje' }
  within('tr.row.row-name') { assert page.has_content? 'Vannlilje' }
  within('tr.row.row-price') { assert page.has_content? '13,37' }
  within('tr.row.row-description') { assert page.has_content? 'Et vakkert veggbilde med en vannlilje' }
  within('tr.row.row-product_category') { assert page.has_content? 'Veggbilder' }
  within('tr.row.row-available_quantity') { assert page.has_content? '7' }
end

And(/^a new product has just been created$/) do
  Product.create(name: 'Vannlilje', price: 13.37, description: 'Et vakkert veggbilde med en vannlilje', product_category_id: 3, inventory_attributes: { available_quantity: 7})
end

When(/^I look up the most recent created product$/) do
  visit store_manager_product_path(Product.last)
end

Then(/^I should see the information for the most recent created product$/) do
  within('h2#page_title') { assert page.has_content? 'Vannlilje' }
  within('tr.row.row-name') { assert page.has_content? 'Vannlilje' }
  within('tr.row.row-price') { assert page.has_content? '13,37' }
  within('tr.row.row-description') { assert page.has_content? 'Et vakkert veggbilde med en vannlilje' }
  within('tr.row.row-product_category') { assert page.has_content? 'Veggbilder' }
  within('tr.row.row-available_quantity') { assert page.has_content? '7' }
end

And(/^a product with values that needs to be changed$/) do
  @product = Product.create(name: 'Blomstervase', price: 47.41, description: 'Et grusomt bilde vi ikke får solgt', product_category_id: 1, inventory_attributes: { available_quantity: 0})
end

And(/^I enter the products edit page$/) do
  visit edit_store_manager_product_path(@product)
end

When(/^I correct the incorrect fields$/) do
  fill_in 'product_name', with: 'Vannlilje'
  fill_in 'product_price', with: '13.37'
  fill_in 'product_description', with: 'Et vakkert veggbilde med en vannlilje'
  select 'Veggbilder', from: 'product_product_category_id'
  fill_in 'product_inventory_attributes_available_quantity', with: '7'
  click_button "Update Produkt"
end

Then(/^the product should have its values updated$/) do
  within('h2#page_title') { assert page.has_content? 'Vannlilje' }
  within('tr.row.row-name') { assert page.has_content? 'Vannlilje' }
  within('tr.row.row-price') { assert page.has_content? '13,37' }
  within('tr.row.row-description') { assert page.has_content? 'Et vakkert veggbilde med en vannlilje' }
  within('tr.row.row-product_category') { assert page.has_content? 'Veggbilder' }
  within('tr.row.row-available_quantity') { assert page.has_content? '7' }
end