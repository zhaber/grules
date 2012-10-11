package org.grules;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import groovy.lang.MapWithDefault;

import org.codehaus.groovy.runtime.MethodClosure;
import org.grules.config.GrulesConfig;

import com.google.inject.Inject;

/**
 * Resource bundle for error messages.
 */
class MessageResourceBundle {

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

  @SuppressWarnings("unused")
  private String getProperty(String key) {
    return messagesBundle.getString(key);
  }

  @SuppressWarnings("unused")
  private String throwMissingResourceException(String key) {
    throw new MissingResourceException("Can not find bundle " + bundlePath, bundlePath, "");
  }

  @Inject
  MessageResourceBundle(GrulesConfig config) {
    bundlePath = config.getResourceBundlePath();
    ResourceBundle resourceBundle;
    MethodClosure methodClosure;
    try {
      resourceBundle = ResourceBundle.getBundle(bundlePath);
      methodClosure = new MethodClosure(this, "getProperty");
    } catch (MissingResourceException e) {
      resourceBundle = NON_EXISTING_BUNDLE;
      methodClosure = new MethodClosure(this, "throwMissingResourceException");
    }
    messages = MapWithDefault.newInstance(new HashMap<String, String>(), methodClosure);
    messagesBundle = resourceBundle;
  }

  public Map<String, String> getMessages() {
    return messages;
  }
}