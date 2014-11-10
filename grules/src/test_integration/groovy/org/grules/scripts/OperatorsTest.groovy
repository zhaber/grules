package org.grules.scripts

import static org.grules.TestScriptEntities.VALID_INTEGER_STRING
import static org.grules.TestScriptEntities.PARAMETER_NAME
import static org.grules.TestScriptEntities.VALID_INTEGER
import static org.grules.TestScriptEntities.PARAMETER_NAME_AUX

import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class OperatorsTest extends Specification {

  def "All operators work according to rules semantics"() {
    setup:
      def parameters = [(PARAMETER_NAME):VALID_INTEGER_STRING, (PARAMETER_NAME_AUX):VALID_INTEGER_STRING]
      RulesScriptResult result = GrulesAPI.applyRules(OperatorsGrules, parameters)
    expect:
      result.cleanParameters[PARAMETER_NAME] == VALID_INTEGER
      result.invalidParameters.containsKey(PARAMETER_NAME_AUX)
  }

}
