class ProductsControllerTest < ActionController::TestCase
  include AssertJson

  test 'should_get_index' do
    get :index
    assert_response :success, 'Index response not successful'
    assert_not_nil assigns(:products), 'Products not assigned on Index'
    assert_nil assigns(:product), 'Product assigned on Index'
  end

  test 'should_get_show' do
    get :show, id: 1
    assert_response :success, 'Show response not successful'
    assert_nil assigns(:products), 'Products assigned on Show'
    assert_not_nil assigns(:product), 'Product not assigned on Show'
  end

  test 'should_get_index_as_json' do
    get :index, format: :json
    assert_response :success, 'Index (json) response not successful'
    assert_not_nil assigns(:products), 'Products not assigned on Index (json)'
    assert_json assigns(:products)
    assert_nil assigns(:product), 'Product assigned on Index (json)'
  end

  test 'should_get_show_as_json' do
    get :show, id: 1, format: :json
    assert_response :success, 'Show (json) response not successful'
    assert_nil assigns(:products), 'Products assigned on Show (json)'
    assert_not_nil assigns(:product), 'Product not assigned on Show (json)'
    assert_json assigns(:product)
  end
end