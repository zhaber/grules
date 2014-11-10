package org.grules.scripts

import static org.grules.TestScriptEntities.PARAMETER_NAME
import static org.grules.TestScriptEntities.PARAMETER_VALUE
import static org.grules.TestScriptEntities.FUNCTION_FOR_LIST
import static org.grules.TestScriptEntities.DEFAULT_VALUE

import org.grules.Grules
import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class ListParametersTest extends Specification {

  def "List parameters"() {
    setup:
      RulesScriptResult result = GrulesAPI.applyRules(ListParametersGrules, [(PARAMETER_NAME):PARAMETER_VALUE])
      def ruleName = 'PARAMETER_NAME' + Grules.COMBINED_PARAMETERS_SEPARATOR + 'PARAMETER_NAME_AUX'
    expect:
      result.cleanParameters.containsKey(ruleName)
      result.cleanParameters[ruleName] == FUNCTION_FOR_LIST([PARAMETER_VALUE, DEFAULT_VALUE])
  }

}
