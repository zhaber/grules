package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class VariablesTest extends Specification {

  def "Variables are accessible from rules"() {
    setup:
      RulesScriptResult result = GrulesAPI.applyRules(VariablesGrules, [(PARAMETER_NAME): VALID_INTEGER])
    expect:
      result.cleanParameters.containsKey(PARAMETER_NAME)
      result.variables.containsKey(VARIABLE_NAME)
      !result.variables.containsKey(PARAMETER_NAME)
  }
}