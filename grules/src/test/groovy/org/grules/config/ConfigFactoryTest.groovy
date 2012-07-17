package org.grules.config

import spock.lang.Specification

class ConfigFactoryTest extends Specification {

	def "Default config read"() {
		setup:
		  def configFactory = new ConfigFactory()
		  def config = configFactory.createDefaultConfig()
		expect:
		  config.logLevel == ConfigFactory.DEFATULT_CONFIG[Config.LOG_LEVEL_PARAMETER_NAME]
	}
}
