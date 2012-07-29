package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.Grules
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class ParametersAnnotationTest extends Specification {

  def "Parameter annotations"() {
    setup:
      RulesScriptResult result = Grules.applyRules(ParameterAnnotationGrules, [:])
    expect:
      result.cleanParameters.containsKey(PARAMETER_NAME)
  }

}