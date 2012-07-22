package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.Grules
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class RequiredTest extends Specification {

  def "Required but missing parameters are reported"() {
    setup:
      RulesScriptResult result = Grules.applyRules(RequiredGrules, [:])
    expect:
      result.cleanParameters == [:]
      PARAMETER_NAME in result.missingRequiredParameters
  }
}
