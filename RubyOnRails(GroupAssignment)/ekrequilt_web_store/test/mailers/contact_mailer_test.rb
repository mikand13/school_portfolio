require 'test_helper'

class ContactMailerTest < ActionMailer::TestCase
  test 'contact' do
    email = ContactMailer.contact('mikand@test.com', 'This is a contact mail!').deliver_now
    assert_not ActionMailer::Base.deliveries.empty?

    assert_equal ['noreply@ekrequilt.no'], email.from
    assert_equal I18n.t('mailer.contact_form'), email.subject
  end
end
