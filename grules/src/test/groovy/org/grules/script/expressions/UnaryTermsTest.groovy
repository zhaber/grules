package org.grules.script.expressions

import static org.grules.TestRuleEntriesFactory.createIsIntegerValidator
import static org.grules.TestRuleEntriesFactory.createTildeTerm
import static org.grules.TestRuleEntriesFactory.createConversionTerm
import static org.grules.TestScriptEntities.INVALID_PARAMETER_VALUE

import org.grules.script.expressions.Operators.TildeTermOperators

import spock.lang.Specification

class UnaryTermsTest extends Specification {

	def "Not term returns opposit result"() {
		setup:
		  def term = new NotTerm(createIsIntegerValidator())
		expect:
		  term.apply(INVALID_PARAMETER_VALUE)
	}

	def "Not operator throws exception if term is tilde term"() {
		when:
			use(TildeTermOperators) {
				-createTildeTerm()
			}
		then:
			thrown(InvalidBooleanTermException)
	}

	def "Not operator throws exception if term returns non-boolean value"() {
		when:
			(new NotTerm(createConversionTerm())).apply('')
		then:
			thrown(InvalidBooleanTermException)
	}

}
