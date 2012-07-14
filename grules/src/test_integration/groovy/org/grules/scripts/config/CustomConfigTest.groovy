package org.grules.scripts.config

import static org.grules.TestScriptEntities.*

import org.grules.config.ConfigFactory

import spock.lang.Specification

class CustomConfigTest extends Specification {
		
	def "Custom config read"() {
		setup:
	  	def config = ConfigFactory.createConfig(CustomConfig)
		expect:
		  config.threadPoolSize == OVERRIDDEN_THREAD_POOL_SIZE
		  config.notValidatedParametersAction == NOT_VALIDATED_PARAMETERS_ACTION
	}
	
	def "Lasci config is read from recources"() {
		setup:
		  def config = ConfigFactory.createConfig()
		expect: 
		  config.threadPoolSize == OVERRIDDEN_THREAD_POOL_SIZE
	}
}