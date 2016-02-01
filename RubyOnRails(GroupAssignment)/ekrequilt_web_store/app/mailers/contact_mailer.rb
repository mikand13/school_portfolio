class ContactMailer < ApplicationMailer
  def contact(email, content)
    @recipients = ['contact@ekrequilt.no'] << email
    @content = content
    mail(to: @recipients, subject: I18n.t('mailer.contact_form'))
  end
end
