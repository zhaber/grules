package org.grules.config

import spock.lang.Specification

class ConfigFactoryTest extends Specification {

	def "Default config read"() {
		setup:
		  def configFactory = new ConfigFactory()
		  def config = configFactory.createDefaultConfig()
		expect:
		  config.threadPoolSize == ConfigFactory.DEFATULT_CONFIG[Config.THREAD_POOL_SIZE_PARAMETER_NAME]
	}
}
