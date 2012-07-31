package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class ErrorMessageTest extends Specification {

  def "Error messages is reported if a request parameter is invalid"() {
    setup:
      def parameters = [(PARAMETER_NAME): PARAMETER_VALUE, (PARAMETER_NAME_AUX): PARAMETER_VALUE_AUX]
      RulesScriptResult result = GrulesAPI.applyRules(ErrorMessagesGrules, parameters)
    expect:
      result.invalidParameters.containsKey(PARAMETER_NAME)
      result.invalidParameters.get(PARAMETER_NAME).message == ERROR_MSG
      result.invalidParameters.containsKey(PARAMETER_NAME_AUX)
      result.invalidParameters.get(PARAMETER_NAME_AUX).message == ERROR_MSG
  }

}
