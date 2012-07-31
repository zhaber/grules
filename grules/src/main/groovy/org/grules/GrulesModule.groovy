package org.grules

import org.grules.config.GrulesConfig
import org.grules.config.GrulesConfigFactory
import org.grules.script.RuleEngine
import org.grules.script.RulesScriptFactory

import com.google.inject.Binder
import com.google.inject.Module
import com.google.inject.Scopes

/**
 * The main Guice module.
 */
class GrulesModule implements Module {

  void configure(Binder binder) {
    binder.bind(GrulesConfig).toInstance(new GrulesConfigFactory().createConfig())
    binder.bind(RulesScriptFactory).in(Scopes.SINGLETON)
    binder.bind(RuleEngine).in(Scopes.SINGLETON)
    binder.bind(MessagesResourceBundle).in(Scopes.SINGLETON)
  }
}




