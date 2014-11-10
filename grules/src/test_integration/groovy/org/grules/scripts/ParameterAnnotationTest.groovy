package org.grules.scripts

import static org.grules.TestScriptEntities.PARAMETER_NAME

import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class ParameterAnnotationTest extends Specification {

  def "Parameter annotation"() {
    setup:
      RulesScriptResult result = GrulesAPI.applyRules(ParameterAnnotationGrules, [:])
    expect:
      result.cleanParameters.containsKey(PARAMETER_NAME)
  }

}
