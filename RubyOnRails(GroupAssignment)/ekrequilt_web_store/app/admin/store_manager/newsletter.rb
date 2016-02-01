ActiveAdmin.register_page 'Newsletter', namespace: :store_manager do
  menu(priority: 100, label: I18n.t('newsletter'))

  content title: I18n.t('newsletter') do
    render 'newsletter_form'
  end
end