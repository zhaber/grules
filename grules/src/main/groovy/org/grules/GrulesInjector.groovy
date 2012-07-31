package org.grules

import org.grules.config.GrulesConfig
import org.grules.script.RuleEngine

import com.google.inject.Guice
import com.google.inject.Injector

/**
 * A Guice injector for service classes.
 */
class GrulesInjector {

  private static final Injector INJECTOR = Guice.createInjector(new GrulesModule())

  static RuleEngine getRuleEngine() {
    INJECTOR.getInstance(RuleEngine)
  }

  static GrulesConfig getConfig() {
    INJECTOR.getInstance(GrulesConfig)
  }

  static MessagesResourceBundle getMessagesResourceBundle() {
    INJECTOR.getInstance(MessagesResourceBundle)
  }
}
