module DeviseHelper
  def devise_error_messages!
    return '' if resource.nil? || resource.errors.empty?

    sentence = I18n.t('errors.messages.not_saved',
                      count: resource.errors.count,
                      resource: resource.class.model_name.human.downcase)
    messages = resource.errors.full_messages.map {
        |message| '<div data-alert class="alert-box alert round alert">' + message + '</div>'}.join

    html = <<-HTML
    <section id="message" class="forms large-8 large-centered columns">
      <div data-alert class="alert-box radius notice">#{sentence}</div>
      #{messages}
    </section>
    HTML

    html.html_safe
  end

  def devise_error_messages?
    resource.errors.empty? ? false : true
  end
end