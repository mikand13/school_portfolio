require 'test_helper'

class NewsletterMailerTest < ActionMailer::TestCase
  test 'news' do
    email = NewsletterMailer.news_broadcast('This is a news mail!').deliver_now
    assert_not ActionMailer::Base.deliveries.empty?

    assert_equal ['noreply@ekrequilt.no'], email.from
    assert_equal I18n.t('mailer.news'), email.subject
  end

  test 'news_products' do
    email = NewsletterMailer.products_news(Product.all).deliver_now
    assert_not ActionMailer::Base.deliveries.empty?

    assert_equal ['noreply@ekrequilt.no'], email.from
    assert_equal I18n.t('mailer.news_products'), email.subject
  end
end
