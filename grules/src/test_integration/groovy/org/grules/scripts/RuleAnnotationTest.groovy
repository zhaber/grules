package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class RuleAnnotationTest extends Specification {

  def "Rule annotation"() {
    setup:
      def parameters = [(PARAMETER_NAME): VALID_INTEGER, (PARAMETER_NAME_AUX): VALID_INTEGER]
      RulesScriptResult result = GrulesAPI.applyRules(RuleAnnotationGrules, parameters)
    expect:
      result.cleanParameters.containsKey(PARAMETER_NAME)
      result.cleanParameters.containsKey(PARAMETER_NAME_AUX)
      result.cleanParameters[PARAMETER_NAME] == FUNCTION_FOR_TWO_ARGUMENTS(VALID_INTEGER, VALID_INTEGER)
      result.cleanParameters[PARAMETER_NAME_AUX] == FUNCTION_FOR_TWO_ARGUMENTS(VALID_INTEGER, VALID_INTEGER)
  }

}