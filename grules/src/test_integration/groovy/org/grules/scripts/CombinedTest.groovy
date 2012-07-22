package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.Grules
import org.grules.GrulesLogger
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class CombinedTest extends Specification {

  def setup() {
    GrulesLogger.turnOff()
  }

  def "Rules are applied correctly to input parameters"() {
    setup:
      def parameters = [
        id: VALID_INTEGER_STRING,
        closure: PARAMETER_VALUE,
        equalToWithDefaultValue: PARAMETER_VALUE,
        invalidParameter: INVALID_PARAMETER_VALUE]
      RulesScriptResult scriptResult = Grules.applyRules(CombinedGrules, parameters)
    expect:
      scriptResult.cleanParameters['id'] == VALID_INTEGER
      scriptResult.cleanParameters['closure'] == PARAMETER_VALUE
      scriptResult.cleanParameters['equalToWithDefaultValue'] == PARAMETER_VALUE
      scriptResult.cleanParameters['withDefaultValue'] == PARAMETER_VALUE
      !scriptResult.cleanParameters.containsKey('invalidParameter')
  }
}
