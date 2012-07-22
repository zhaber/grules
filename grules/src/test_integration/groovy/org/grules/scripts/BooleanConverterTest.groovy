package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.Grules
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class BooleanConverterTest extends Specification {

  def "Converter annotation wraps result in boolean wrapper"() {
    setup:
      RulesScriptResult result = Grules.applyRules(BooleanConverterGrules, [(PARAMETER_NAME): FALSE_PARAMETER])
    expect:
      result.cleanParameters.containsKey(PARAMETER_NAME)
      result.cleanParameters.get(PARAMETER_NAME)
  }
}
