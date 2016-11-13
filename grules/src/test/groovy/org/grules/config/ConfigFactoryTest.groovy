package org.grules.config

import static org.grules.TestScriptEntities.TEST_CONFIG
import spock.lang.Specification

class ConfigFactoryTest extends Specification {

  def "Default config can be read"() {
    expect:
      TEST_CONFIG.logLevel == DefaultGrulesConfig.INSTANCE.parameters[GrulesConfig.LOG_LEVEL_PARAMETER_NAME]
  }
}
