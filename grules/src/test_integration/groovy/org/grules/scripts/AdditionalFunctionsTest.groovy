package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.Grules
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class AdditionalFunctionsTest extends Specification {

	def "Additional functions are defined as script variables"() {
		setup:
		  def parameters = [(PARAMETER_NAME): PARAMETER_VALUE]
			def functions = [(ADDITIONAL_FUNCTION): {it == PARAMETER_VALUE}]
		  RulesScriptResult result = Grules.applyRules(AdditionalFunctionsGrules, parameters, functions)
		expect:
		  result.cleanParameters.containsKey(PARAMETER_NAME)
	}
	
}