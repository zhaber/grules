package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.Grules
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class EnvironmentTest extends Specification {

  def "Additional script environment can be set"() {
    setup:
      def parameters = [(PARAMETER_NAME): VARIABLE_VALUE]
      def function = {parameterValue, variableValue -> parameterValue == variableValue}
      def environment = [(FUNCTION_NAME): function, (VARIABLE_NAME): VARIABLE_VALUE]
      RulesScriptResult result = Grules.applyRules(EnvironmentGrules, parameters, environment)
    expect:
      result.cleanParameters.containsKey(PARAMETER_NAME)
  }

}
