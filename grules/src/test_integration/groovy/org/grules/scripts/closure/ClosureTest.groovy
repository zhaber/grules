package org.grules.scripts.closure

import static org.grules.TestScriptEntities.PARAMETER_NAME_AUX
import static org.grules.TestScriptEntities.VALID_INTEGER
import static org.grules.TestScriptEntities.PARAMETER_NAME
import static org.grules.TestScriptEntities.INVALID_PARAMETER_VALUE

import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class ClosureTest extends Specification {

  def "Both validation and conversion closures are handled properly"() {
    setup:
      def parameters = [(PARAMETER_NAME):VALID_INTEGER, (PARAMETER_NAME_AUX):INVALID_PARAMETER_VALUE]
      RulesScriptResult result = GrulesAPI.applyRules(ClosureGrules, parameters)
    expect:
      result.cleanParameters.containsKey(PARAMETER_NAME)
      result.cleanParameters.get(PARAMETER_NAME) instanceof Boolean && !result.cleanParameters.get(PARAMETER_NAME)
      result.invalidParameters.containsKey(PARAMETER_NAME_AUX)
  }
}
