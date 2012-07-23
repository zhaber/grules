package org.grules.config

import static org.grules.TestScriptEntities.*
import spock.lang.Specification

class ConfigFactoryTest extends Specification {

	def "Default config read"() {
		expect:
		  DEFAULT_CONFIG.logLevel == ConfigFactory.DEFATULT_CONFIG[Config.LOG_LEVEL_PARAMETER_NAME]
	}
}
