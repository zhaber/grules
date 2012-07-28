package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.Grules
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class IfConditionTest extends Specification {

  def "List parameters"() {
    setup:
      RulesScriptResult result = Grules.applyRules(IfConditionGrules, [(PARAMETER_NAME): PARAMETER_VALUE])
    expect:
      result.cleanParameters.containsKey(PARAMETER_NAME)
  }

}