When(/^I select the products I want to buy$/) do
  visit products_path(category: 2)
  within '.products-show' do
    find('tr:nth-child(1) td:nth-child(2) a.product-link').click
  end
  find('#add-to-cart').click

  visit products_path(category: 1)
  within '.products-show' do
    find('tr:nth-child(1) td:nth-child(1) a.product-link').click
  end
  find('#add-to-cart').click
end

Then(/^I should see all the products I have selected in my shopping cart$/) do
  assert page.has_content? "#{I18n.t('cart.name')} (2)"

  find('#cart-link').click

  assert page.has_css? '#line_items tr:nth-child(2)'
  assert page.has_css? '#line_items tr:nth-child(3)'

  assert find('#line_items tr:nth-child(4)').text, 'Totalt:'
end