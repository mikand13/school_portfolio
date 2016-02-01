# noinspection RubyResolve,RubyResolve,RubyResolve
class Order < ActiveRecord::Base
  require 'active_merchant/billing/rails'
  include Sellable

  belongs_to :user
  has_many :order_transactions

  attr_accessor :card_number, :card_verification

  enum status: { unprocessed: 0, authorized: 1, captured: 2, refunded: 3, voided: 4 }

  validates :user_id, :cart_id, :ip_address, :first_name, :last_name, presence: true
  validates :street, :city, :state, :zip, :card_brand, :card_expires_on, presence: true if @credit_card
  validates :line_items, presence: true
  validate :validate_card, on: :create

  def authorize
    response = process_authorize
    order_transactions.create!(action: 'authorize', amount: price_in_minimum_currency, response: response)

    if response.success?
      line_items.each { |l| l.product.inventory.transfer_to_pending_sales(l.quantity)}
      update(purchased_at: Time.now, open: true)
      authorized!
    else
      unprocessed!
    end

    response.success?
  end

  def capture
    if authorized?
      response = process_capture
      order_transactions.create!(action: 'capture', response: response)

      if response.success?
        line_items.each { |l| l.product.inventory.finalize_sale(l.quantity)}
        close_order
        captured!
      end

      response.success?
    end
  end

  def refund
    if captured?
      response = process_refund
      order_transactions.create!(action: 'refund', response: response)

      if response.success?
        close_order
        refunded!
      end

      response.success?
    end
  end

  def void_authorization
    if authorized?
      response = process_void
      order_transactions.create!(action: 'void', amount: price_in_minimum_currency, response: response)

      if response.success?
        line_items.each { |l| l.product.inventory.make_available(l.quantity)}
        close_order
        voided!
      end

      response.success?
    end
  end

  def express_token=(token)
    write_attribute(:express_token, token)

    if new_record? && !token.blank?
      details = EXPRESS_PAYPAL_GATEWAY.details_for(token)
      self.express_payer_id = details.payer_id
      self.first_name = details.params['first_name']
      self.last_name = details.params['last_name']
      self.street = details.params['street1']

      unless details.params['street2'].nil?
        self.street += ' '
        self.street += details.params['street2']
      end

      self.city = details.params['city_name']
      self.state = details.params['state_or_province']
      self.zip = details.params['postal_code']
      self.country = details.params['country_name']
    end
  end

  def validate_card
    if express_token.blank?
      if credit_card.valid?
        true
      else
        credit_card.errors.full_messages.each do |message|
          errors.add('credit_card_error', message)
        end
        false
      end
    else
      true
    end
  end

  def transfer_line_items_from_cart (cart_line_items)
    cart_line_items.each do |clt|
      line_items << LineItem.create(product: clt.product, sellable: self, quantity: clt.quantity)
    end
  end

  def open?
    open
  end

  def display_name
    "#{I18n.t('activerecord.models.order.one')} ##{id} - #{ActionController::Base.helpers.number_to_currency(price)}"
  end

  def self.completed
    Order.where(open: false).order('created_at ASC')
  end

  def self.pending
    Order.where(open: true).order('created_at ASC')
  end

  def self.cancelled
    Order.where(status: 3)
  end

  def self.refunded
    Order.where(status: 2)
  end

  def self.find_x_most_recent(amount)
    Order.where(open: true).order('created_at ASC').limit(amount)
  end

  private

  def close_order
    update_attribute(:closed_at, Time.now)
    update_attribute(:open, false)
  end

  def process_authorize
    express_token ?
      EXPRESS_PAYPAL_GATEWAY.authorize(price_in_minimum_currency, express_purchase_options) :
      STANDARD_PAYPAL_GATEWAY.authorize(price_in_minimum_currency, credit_card, purchase_options)
  end

  def process_capture
    express_token ?
      EXPRESS_PAYPAL_GATEWAY.capture(price_in_minimum_currency, authorize_authorization) :
      STANDARD_PAYPAL_GATEWAY.capture(price_in_minimum_currency, authorize_authorization)
  end

  # nil for full refund
  # amount in minimum currency for partial
  def process_refund
    express_token ?
      EXPRESS_PAYPAL_GATEWAY.refund(nil, capture_authorization) :
      STANDARD_PAYPAL_GATEWAY.refund(nil, capture_authorization)
  end

  def process_void
    express_token ?
      EXPRESS_PAYPAL_GATEWAY.void(authorize_authorization) :
      STANDARD_PAYPAL_GATEWAY.void(authorize_authorization)
  end

  def authorize_authorization
    order_transactions.where('action = ?', 'authorize').where('success = ?', true).first.authorization
  end

  def capture_authorization
    order_transactions.where('action = ?', 'capture').where('success = ?', true).first.authorization
  end

  def express_purchase_options
    {
        ip: ip_address,
        token: express_token,
        payer_id: express_payer_id,
        items: items
    }
  end

  def purchase_options
    # hack for norwegian post system, any other country with same system could be added here
    unless I18n.locale != :nb
      state = city
    end

    {
        ip: ip_address,
        billing_address: {
            name: "#{:first_name} #{:last_name}",
            address1: :street,
            city: :city,
            state: :state,
            country: :country,
            zip: :zip
        }
    }
  end

  def credit_card
    @credit_card ||= ActiveMerchant::Billing::CreditCard.new(
        brand:              card_brand,
        number:             card_number,
        verification_value: card_verification,
        month:              card_expires_on.month,
        year:               card_expires_on.year,
        first_name:         first_name,
        last_name:          last_name
    )
  end
end
