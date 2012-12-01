package org.grules;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import groovy.lang.Closure;
import groovy.lang.MapWithDefault;

import org.grules.config.GrulesConfig;

import com.google.inject.Inject;

/**
 * Resource bundle for error messages.
 */
public class MessageResourceBundle {

  private final Map<String, String> messages;
  private final ResourceBundle messagesBundle;
  private final String bundlePath;

  private final static ResourceBundle NON_EXISTING_BUNDLE = new ResourceBundle() {

    @Override
    public Enumeration<String> getKeys() {
      throw new IllegalStateException();
    }

    @Override
    protected Object handleGetObject(String key) {
      throw new IllegalStateException();
    }

    @Override
    public String toString() {
      throw new IllegalStateException();
    }
  };

  @Inject
  MessageResourceBundle(GrulesConfig config) {
    bundlePath = config.getResourceBundlePath();
    ResourceBundle resourceBundle;
    Closure<String> methodClosure;
    try {
      resourceBundle = ResourceBundle.getBundle(bundlePath);
      methodClosure = new Closure<String>(this) {
        @SuppressWarnings("unused")
        private String doCall(String key) {
          return messagesBundle.getString(key);
        }
      };
    } catch (MissingResourceException e) {
      resourceBundle = NON_EXISTING_BUNDLE;
      methodClosure = new Closure<String>(this) {
        @SuppressWarnings("unused")
        private String doCall(String key) {
          throw new MissingResourceException("Cannot find resource bundle " + bundlePath, bundlePath, "");
        }
      };
    }
    messages = MapWithDefault.newInstance(new HashMap<String, String>(), methodClosure);
    messagesBundle = resourceBundle;
  }

  public Map<String, String> getMessages() {
    return messages;
  }
}