package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.Grules
import org.grules.script.RulesScriptResult

import spock.lang.Specification

class TypeConversionTest extends Specification {
	
	def "Types are converted"() {
		setup:
	    RulesScriptResult result = Grules.applyRules(TypeConversionGrules, (PARAMETER_NAME): VALID_INTEGER)
	  expect:
		  result.cleanParameters.containsKey(PARAMETER_NAME) 
	}
}