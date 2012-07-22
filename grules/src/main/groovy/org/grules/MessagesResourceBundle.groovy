package org.grules

import org.grules.config.Config

import com.google.inject.Inject

/**
 * Resource bundle for error messages.
 */
class MessagesResourceBundle {

  final Map<String, String> messages

  @Inject
  MessagesResourceBundle(Config config) {
    String bundlePath = config.resourceBundlePath
    try {
      ResourceBundle resourceBundle = ResourceBundle.getBundle(bundlePath)
      messages = [:].withDefault {
        String key ->
        resourceBundle.getString(key)
      }
    }	catch(MissingResourceException e) {
      messages = [:].withDefault {
        throw new MissingResourceException("Can not find bundle $bundlePath", bundlePath, '')
      }
    }
  }
}
