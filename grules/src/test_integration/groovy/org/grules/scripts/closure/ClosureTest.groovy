package org.grules.scripts.closure

import static org.grules.TestScriptEntities.*

import org.grules.Grules
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class ClosureTest extends Specification {
	
	def "Both validation and conversion closures are handled properly"() {
	  setup:
		  def parameters = [(PARAMETER_NAME): VALID_INTEGER, (PARAMETER_NAME_AUX): INVALID_PARAMETER_VALUE]
		  RulesScriptResult result = Grules.applyRules(ClosureGrules, parameters)
	  expect:
		  result.cleanParameters.containsKey(PARAMETER_NAME)
			result.cleanParameters.get(PARAMETER_NAME) instanceof Boolean && !result.cleanParameters.get(PARAMETER_NAME) 
			result.invalidParameters.containsKey(PARAMETER_NAME_AUX)
	}
}