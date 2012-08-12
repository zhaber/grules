package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class RuleExtensionTest extends Specification {

  def "Rule annotation"() {
    setup:
      RulesScriptResult result = GrulesAPI.applyRules(RuleExtensionGrules, [(PARAMETER_NAME): VALID_INTEGER])
      def expectedResult = FUNCTION_FOR_ONE_ARGUMENT(FUNCTION_FOR_ONE_ARGUMENT(VALID_INTEGER))
    expect:
      result.cleanParameters.containsKey(PARAMETER_NAME)
      result.cleanParameters[PARAMETER_NAME] == expectedResult
  }

}