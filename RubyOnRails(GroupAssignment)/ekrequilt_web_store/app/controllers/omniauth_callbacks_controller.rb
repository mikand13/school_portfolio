# noinspection RubyResolve
class OmniauthCallbacksController < Devise::OmniauthCallbacksController
  def facebook
    bind_omniauth_to_account(name: 'Facebook', code: 'facebook')
  end

  def gplus
    bind_omniauth_to_account(name: 'Google+', code: 'gplus')
  end

  def bind_omniauth_to_account(params)
    account = Account.from_omniauth(request.env['omniauth.auth'])

    if account.persisted?
      bind_to_user(account)
      sign_in_and_redirect account, event: :authentication
      set_flash_message(:notice, :success, kind: params[:name]) if is_flashing_format?
    else
      session['devise.' + params[:code] + '_data'] = request.env['omniauth.auth']
      redirect_to new_account_registration_url
    end
  end

  def bind_to_user(account)
    matching_account = Account.where('email = ?', account.email).where.not('provider = ?', account.provider).first

    if matching_account
      account.user_id = matching_account.user_id
    else
      user = User.create(first_name: account.first_name, last_name: account.last_name, full_name: account.full_name)
      user.roles << Role.find_by_name(:customer)
      account.user_id = user.id
    end
  end
end