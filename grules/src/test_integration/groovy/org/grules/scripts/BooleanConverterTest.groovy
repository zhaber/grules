package org.grules.scripts

import static org.grules.TestScriptEntities.PARAMETER_NAME
import static org.grules.TestScriptEntities.FALSE_PARAMETER

import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class BooleanConverterTest extends Specification {

  def "Converter annotation wraps result in boolean wrapper"() {
    setup:
      RulesScriptResult result = GrulesAPI.applyRules(BooleanConverterGrules, [(PARAMETER_NAME):FALSE_PARAMETER])
    expect:
      result.cleanParameters.containsKey(PARAMETER_NAME)
      result.cleanParameters.get(PARAMETER_NAME)
  }
}
