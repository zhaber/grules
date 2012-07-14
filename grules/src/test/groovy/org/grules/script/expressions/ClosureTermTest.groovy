package org.grules.script.expressions

import static org.grules.TestRuleEntriesFactory.*
import static org.grules.TestScriptEntities.*
import spock.lang.Specification

class ClosureTermTest extends Specification {
	
	def "Closure term applies method closure"() {
		setup:
		  Term term = newIsEmptyValidator()
    expect:
		  term.apply('')
	}
}
