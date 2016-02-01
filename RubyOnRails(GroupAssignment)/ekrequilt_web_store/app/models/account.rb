class Account < ActiveRecord::Base
  belongs_to :user

  devise :database_authenticatable, :registerable, :confirmable, :password_archivable,
         :recoverable, :rememberable, :trackable, :lockable, :timeoutable,
         :omniauthable, omniauth_providers: [:facebook, :gplus]

  validates_presence_of   :email, if: :email_required?
  validates_uniqueness_of :email, scope: :user_id, allow_blank: true, if: :email_changed?
  validates_format_of     :email, with: Devise.email_regexp, allow_blank: true, if: :email_changed?
  #validates :email, email: true # mx/idn verification, currently too laggy, dont know why

  def self.from_omniauth(auth)
    where(provider: auth.provider, uid: auth.uid).first_or_create do |account|
      account.confirm! # Cancels confirmation mail for omniauth

      account.email = auth.info.email
      account.password = Devise.friendly_token[0,20]
      account.first_name = auth.info.first_name
      account.last_name = auth.info.last_name
      account.full_name = auth.info.name
      account.avatar_url = auth.info.image
    end
  end

  def self.find_by_confirmation_token (token)
    Account.where('confirmation_token = ?', token).first
  end

  def self.find_by_confirmation_token! (token)
    Account.where('confirmation_token = ?', token).first
  end

  def attempt_set_password(params)
    p = {}
    p[:password] = params[:password]
    p[:password_confirmation] = params[:password_confirmation]

    old_p = {password: password, password_confirmation: password_confirmation}
    update_attributes(p)
    update_attributes(old_p) unless password_valid?
  end

  def has_no_password?
    self.encrypted_password.blank?
  end

  def only_if_unconfirmed
    pending_any_confirmation {yield}
  end

  def password_valid?
    self.errors[:password] << I18n.t('cannot_be_empty') if password.blank?
    self.errors[:password_confirmation] << I18n.t('cannot_be_empty') if password_confirmation.blank?
    self.errors[:password_confirmation] << I18n.t('passwords_not_equal') if password != password_confirmation
    self.errors[:password_length] << I18n.t('too_short') if
        password.length < Devise.password_length.min || password.length > Devise.password_length.max
    self.errors[:password_format] << I18n.t('must_include') unless Devise.password_regex.match(password)
    password == password_confirmation && !password.blank? &&
        password.length >= Devise.password_length.min && password.length <= Devise.password_length.max &&
            Devise.password_regex.match(password)
  end

  def names?
    self.errors[:first_name] << I18n.t('cannot_be_empty') if first_name.blank?
    self.errors[:last_name] << I18n.t('cannot_be_empty') if last_name.blank?
  end

  def confirmed?
    !confirmed_at.nil?
  end

  protected

  # for validations #
  def password_required?
    super if password_required?
  end

  def email_required?
    true
  end
  # for validations #

  def timeout_in #timeout
      2.hours
  end
end