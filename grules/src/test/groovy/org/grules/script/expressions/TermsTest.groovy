package org.grules.script.expressions

import static org.grules.TestRuleEntriesFactory.*
import static org.grules.TestScriptEntities.*
import spock.lang.Specification

class TermsTest extends Specification {
	
	def "wrap for term"() {
		setup:
		  def term = TermWrapper.wrap(newTerm())
		expect:
		  term instanceof Term
	}
	
	def "wrap for closure"() {
		setup:
		  def closure = {}
		  def term = TermWrapper.wrap(closure)
		expect:
		  (term as ClosureTerm).closure == closure
	}
	
	def "wrap for null"() {
		when:
		  TermWrapper.wrap(null)
		then:
		  thrown(InvalidTermException) 
	}
	
	def "wrap for invalid term"() {
		when:
		  TermWrapper.wrap(0)
		then:
		  thrown(InvalidTermException)
	}
}
