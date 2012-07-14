package org.grules

import org.grules.config.Config
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
	
	static Config getConfig() {
		INJECTOR.getInstance(Config)
	}
	
	static MessagesResourceBundle getMessagesResourceBundle() {
		INJECTOR.getInstance(MessagesResourceBundle)
	}
}