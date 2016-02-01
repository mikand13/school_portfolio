Rails.application.routes.draw do
  get 'errors/file_not_found'

  get 'errors/unprocessable'

  get 'errors/internal_server_error'

  ActiveAdmin.routes(self)

  unless Rails.env.production?
    get 'design' => 'design#index'
  end

  resources :contact, only: [:new, :create]

  resources :users, only: :show

  resources :newsletters, only: [:create, :destroy] do
    post 'deliver', on: :new
  end

  resources :products, only: [:index, :show]

  resources :line_items, only: :create

  resources :carts, as: :user_cart, only: [:show, :destroy]

  resources :orders, only: [:show, :create, :new, :destroy] do
    get 'express', on: :new
  end

  devise_for :admin_accounts, controllers: { sessions: 'admin_users_sessions'}

  devise_for :accounts,
             controllers: { omniauth_callbacks: 'omniauth_callbacks',
                             confirmations: 'confirmations',
                             registrations: 'registrations',
                             sessions: 'users_sessions',
                             passwords: 'passwords' ,
                             unlocks: 'unlocks' }

  devise_scope :account do
    put 'accounts/confirmation/confirm', to: 'confirmations#confirm'
  end

  match '/404', to: 'errors#file_not_found', via: :all
  match '/422', to: 'errors#unprocessable', via: :all
  match '/500', to: 'errors#internal_server_error', via: :all

  root 'welcome#index'
end
