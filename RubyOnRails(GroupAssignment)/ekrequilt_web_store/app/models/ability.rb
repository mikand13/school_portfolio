class Ability
  include CanCan::Ability

  def initialize(user)
    user ||= User.new # guest user

    if user.role? :admin
      can :manage, :all
    elsif user.role? :store_manager
      can [:create, :read, :update], Product
      can :read, User
    elsif user.role? :customer
      can :read, [Product, User]
      can :read, Cart, id: user.cart.id
      can [:read, :destroy], Order, user_id: user.id
    elsif user.role? :guest
      can :read, [Product, User]
      can :read, Cart, id: user.cart.id
      can :read, Order, user_id: user.id
    end
  end
end
