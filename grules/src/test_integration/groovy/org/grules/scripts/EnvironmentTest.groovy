package org.grules.scripts

import static org.grules.TestScriptEntities.PARAMETER_NAME
import static org.grules.TestScriptEntities.FUNCTION_NAME
import static org.grules.TestScriptEntities.VARIABLE_VALUE
import static org.grules.TestScriptEntities.VARIABLE_NAME

import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class EnvironmentTest extends Specification {

  def "Additional script environment can be set"() {
    setup:
      def parameters = [(PARAMETER_NAME):VARIABLE_VALUE]
      def function = { parameterValue, variableValue -> parameterValue == variableValue }
      def environment = [(FUNCTION_NAME):function, (VARIABLE_NAME):VARIABLE_VALUE]
      RulesScriptResult result = GrulesAPI.applyRules(EnvironmentGrules, parameters, environment)
    expect:
      result.cleanParameters.containsKey(PARAMETER_NAME)
  }
}
