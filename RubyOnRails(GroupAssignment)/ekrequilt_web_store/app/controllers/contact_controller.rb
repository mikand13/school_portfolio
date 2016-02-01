class ContactController < ApplicationController
  def new
  end

  def create
    params = permitted_params

    ContactMailer.contact(params[:email], params[:content]).deliver_now

    flash[:notice] = I18n.t('mailer.thank_you_for_your_contact')

    redirect_to root_path
  end

  private

  def permitted_params
    params.permit(:email, :content)
  end
end