package org.grules.scripts.ast

import static org.grules.TestScriptEntities.*

import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class AstTest extends Specification {

  def "Ast transformation is applied to scripts"() {
    setup:
      RulesScriptResult result = GrulesAPI.applyRules(AstGrules, [(PARAMETER_NAME): VALID_INTEGER_STRING])
    expect:
      result.cleanParameters.get(PARAMETER_NAME) == VALID_INTEGER
  }
}