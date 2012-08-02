package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class SkipTest extends Specification {

  def "Default rules can be skipped"() {
    setup:
      def parameterValue = ' ' + PARAMETER_VALUE
      RulesScriptResult result = GrulesAPI.applyRules(SkipGrules, [(PARAMETER_NAME): parameterValue])
    expect:
      result.cleanParameters[PARAMETER_NAME] == parameterValue
  }
}