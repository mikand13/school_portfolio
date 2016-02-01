class NewslettersController < ApplicationController
  before_filter :authenticate_admin_account!, only: [:deliver]
  before_filter :authenticate_account!, only: [:create, :destroy]

  def deliver
    NewsletterMailer.news_broadcast(params[:content]).deliver_now

    flash[:notice] = I18n.t('newsletter_sent')
    redirect_to store_manager_root_path
  end

  def create
    @user.subscribed_newsletter = true
    @user.save

    redirect_to user_path(@user)
  end

  def destroy
    @user.subscribed_newsletter = false
    @user.save

    redirect_to user_path(@user), status: 303
  end
end