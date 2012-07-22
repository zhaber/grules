package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.Grules
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class OperatorsTest extends Specification {

  def "All operators work according to rules semantics"() {
    setup:
      def parameters = [(PARAMETER_NAME): VALID_INTEGER_STRING, (PARAMETER_NAME_AUX): VALID_INTEGER_STRING]
      RulesScriptResult result = Grules.applyRules(OperatorsGrules, parameters)
    expect:
      result.cleanParameters[PARAMETER_NAME] == VALID_INTEGER
      result.invalidParameters.containsKey(PARAMETER_NAME_AUX)
  }

}
