package org.grules.script.expressions

import static org.grules.TestRuleEntriesFactory.*
import static org.grules.TestScriptEntities.*

import org.grules.script.expressions.Operators.TermOperators
import org.grules.script.expressions.Operators.TildeTermOperators

import spock.lang.Specification

class UnaryTermsTest extends Specification {
	
	def "Not term returns opposit result"() {
		setup:
		  def term = new NotTerm(newIsIntegerValidator()) 
		expect:
		  term.apply(INVALID_PARAMETER)
	}
	
	def "Not operator throws exception if term is tilde term"() {
		when:
			use(TildeTermOperators) {
				-newTildeTerm()
			}
		then:
			thrown(InvalidBooleanTermException)
	}
	
	def "Not operator throws exception if term returns non-boolean value"() {
		when:
			(new NotTerm(newConversionTerm())).apply('')
		then:
			thrown(InvalidBooleanTermException)
	}

}
