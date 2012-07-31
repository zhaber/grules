package org.grules.config

import static org.grules.TestScriptEntities.*
import spock.lang.Specification

class ConfigFactoryTest extends Specification {

	def "Default config can be read"() {
		expect:
		  CONFIG.logLevel == DefaultGrulesConfig.INSTANCE.parameters[GrulesConfig.LOG_LEVEL_PARAMETER_NAME]
	}
}