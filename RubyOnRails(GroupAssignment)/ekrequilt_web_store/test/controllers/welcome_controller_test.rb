class WelcomeControllerTest < ActionController::TestCase
  test 'should_get_index' do
    get :index
    assert_response :success
  end
end