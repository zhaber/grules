package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class ParametersAnnotationTest extends Specification {

  def "Parameter annotations"() {
    setup:
      RulesScriptResult result = GrulesAPI.applyRules(ParameterAnnotationGrules, [:])
    expect:
      result.cleanParameters.containsKey(PARAMETER_NAME)
  }

}