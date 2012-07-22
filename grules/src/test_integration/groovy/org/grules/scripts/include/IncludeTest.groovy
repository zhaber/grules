package org.grules.scripts.include

import static org.grules.TestScriptEntities.*

import org.grules.Grules
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class IncludeTest extends Specification {

  def "Scripts can be included in each other"() {
    setup:
      RulesScriptResult scriptResult = Grules.applyRules(IncludeMainGrules, [(PARAMETER_NAME): PARAMETER_VALUE])
    expect:
      scriptResult.cleanParameters[PARAMETER_NAME] == PARAMETER_VALUE
  }

  def "Scripts can be included with condition"() {
    setup:
      def parameters = [(PARAMETER_NAME): PARAMETER_VALUE]
      RulesScriptResult scriptResult = Grules.applyRules(ConditionalIncludeGrules, parameters)
    expect:
      scriptResult.cleanParameters[PARAMETER_NAME] == PARAMETER_VALUE
  }

}
