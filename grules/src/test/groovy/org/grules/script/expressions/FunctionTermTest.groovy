package org.grules.script.expressions

import static org.grules.TestScriptEntities.*
import spock.lang.Specification

class FunctionTermTest extends Specification {

	def "Function term applies method closure"() {
		setup:
		  def functionTerm = new FunctionTerm({it}, FUNCTION_NAME)
		expect:
			functionTerm.apply(VALID_PARAMETER) == VALID_PARAMETER
	}
}
