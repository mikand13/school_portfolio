FactoryGirl.define do
  factory :admin_account do
    email 'test.admin.ekrequilt@ekrequilt.no'
    encrypted_password 'test_password_bogus'
  end

  factory :faulty_admin_account, class: AdminAccount do
    email 'test.admin.ekrequilt@ekrequilt.no'
  end
end
