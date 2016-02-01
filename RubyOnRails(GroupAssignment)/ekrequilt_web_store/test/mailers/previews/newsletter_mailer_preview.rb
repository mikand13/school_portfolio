# Preview all emails at http://localhost:3000/rails/mailers/newsletter_mailer
class NewsletterMailerPreview < ActionMailer::Preview
  def news
    NewsletterMailer.news_broadcast('This is a news mail!')
  end

 def news_products
    NewsletterMailer.products_news(Product.all)
  end
end
