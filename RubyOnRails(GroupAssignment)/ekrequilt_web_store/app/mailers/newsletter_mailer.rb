class NewsletterMailer < ApplicationMailer
  def news_broadcast(news)
    @news = news
    mail(to: subscribers, subject: I18n.t('mailer.news'))
  end

  def products_news(products)
    @products = products
    mail(to: subscribers, subject: I18n.t('mailer.news_products'))
  end

  private

  def subscribers
    User.joins(:accounts).where('subscribed_newsletter = ?', true).pluck(:email)
  end
end
