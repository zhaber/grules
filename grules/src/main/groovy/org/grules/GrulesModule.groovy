package org.grules

import org.grules.config.Config
import org.grules.config.ConfigFactory
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
		binder.bind(Config).toInstance(ConfigFactory.createConfig())
		binder.bind(RulesScriptFactory).in(Scopes.SINGLETON)
		binder.bind(RuleEngine).in(Scopes.SINGLETON)
		binder.bind(MessagesResourceBundle).in(Scopes.SINGLETON)
	}
}




