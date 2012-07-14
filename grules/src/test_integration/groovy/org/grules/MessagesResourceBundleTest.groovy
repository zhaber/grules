package org.grules

import org.grules.config.Config
import org.grules.config.ConfigFactory

import spock.lang.Specification

class MessagesResourceBundleTest extends Specification {

	def "Resource bundle is read if exists"() {
		setup:
			def resourceBundle = new MessagesResourceBundle(ConfigFactory.createDefaultConfig())
		expect:
			resourceBundle.messages[TestScriptEntities.ERROR_MSG] == TestScriptEntities.ERROR_MSG
	}
	
	def "MissingResourceException is not thrown if messages are not read from resource bundle"() {
		setup:
			Config config = Mock()
			config.resourceBundlePath >> ''
	  when:
			new MessagesResourceBundle(config)
		then:
			notThrown(MissingResourceException)
	}
	
	def "MissingResourceException is thrown on message read"() {
		setup:
			Config config = Mock()
			config.resourceBundlePath	 >> ''
			def resourceBundle = new MessagesResourceBundle(config)
	  when:
			resourceBundle.messages.get('')
		then:
			thrown(MissingResourceException)
	}
}
