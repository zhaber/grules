package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class ValidationBlockTest extends Specification {

  def "Return from validation blocks is added to script result"() {
    setup:
      def parameters = [(PARAMETER_NAME): PARAMETER_VALUE, (PARAMETER_NAME_AUX): INVALID_PARAMETER_VALUE]
      RulesScriptResult result = GrulesAPI.applyRules(ValidationBlockGrules, parameters)
    expect:
      result.cleanParameters.containsKey(PARAMETER_NAME)
      result.cleanParameters[PARAMETER_NAME] == PARAMETER_VALUE
      !result.cleanParameters.containsKey(PARAMETER_NAME_AUX)
      result.invalidParameters[PARAMETER_NAME_AUX].value == INVALID_PARAMETER_VALUE
      result.invalidParameters[PARAMETER_NAME_AUX].errorId == ERROR_ID
      result.invalidParameters[PARAMETER_NAME_AUX].message == ERROR_MESSAGE
      result.invalidParameters[PARAMETER_NAME_AUX].parameter == PARAMETER_NAME_AUX
  }
}
