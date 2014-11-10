package org.grules.scripts

import static org.grules.TestScriptEntities.PARAMETER_NAME_AUX
import static org.grules.TestScriptEntities.PARAMETER_VALUE_AUX
import static org.grules.TestScriptEntities.PARAMETER_NAME
import static org.grules.TestScriptEntities.PARAMETER_VALUE
import static org.grules.TestScriptEntities.ERROR_ID

import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class ErrorsTest extends Specification {

  def "Error id is reported if a request parameter is invalid"() {
    setup:
      def parameters = [(PARAMETER_NAME):PARAMETER_VALUE, (PARAMETER_NAME_AUX):PARAMETER_VALUE_AUX]
      RulesScriptResult result = GrulesAPI.applyRules(ErrorsGrules, parameters)
    expect:
      result.invalidParameters.containsKey(PARAMETER_NAME)
      result.invalidParameters.get(PARAMETER_NAME).errorId == ERROR_ID
      result.invalidParameters.containsKey(PARAMETER_NAME_AUX)
      result.invalidParameters.get(PARAMETER_NAME_AUX).errorId == ERROR_ID
  }

}
