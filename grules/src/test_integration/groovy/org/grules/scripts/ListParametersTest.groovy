package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.Grules
import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class ListParametersTest extends Specification {

  def "List parameters"() {
    setup:
      RulesScriptResult result = GrulesAPI.applyRules(ListParametersGrules, [(PARAMETER_NAME): PARAMETER_VALUE])
      def ruleName = 'PARAMETER_NAME' + Grules.COMBINED_PARAMETERS_SEPARATOR + 'PARAMETER_NAME_AUX'
    expect:
      result.cleanParameters.containsKey(ruleName)
      result.cleanParameters[ruleName] == PARAMETER_VALUE + JOIN_SEPARATOR + DEFAULT_VALUE
  }

}