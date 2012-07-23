package org.grules.script.expressions

import static org.grules.TestRuleEntriesFactory.*
import static org.grules.TestScriptEntities.*

import org.grules.ValidationErrorProperties
import org.grules.ValidationException

import spock.lang.Specification

class SubruleTest extends Specification{
	
	def "Apply validation subrule to valid term"() {
		setup:
		  def validatorTerm = createIsIntegerValidator()
		  def subrule = SubrulesFactory.create(validatorTerm)
		when:
		  subrule.apply(VALID_INTEGER_STRING)
		then:
		  notThrown(ValidationException)
	}

	def "Apply validation subrule to invalid term"() {
		setup:
		  def validatorTerm = createIsIntegerValidator()
		  def subrule = SubrulesFactory.create(validatorTerm, new ValidationErrorProperties())
		when:
		  subrule.apply(INVALID_PARAMETER_VALUE)
		then:
		  thrown(ValidationException)
	}
	
	def "Apply conversion subrule to valid term"() {
		setup:
		  def conversionTerm = createTrimConverter()
		  def subrule = SubrulesFactory.create(conversionTerm)
		when:
		  subrule.apply(DEFAULT_VALUE)
		then:
		  notThrown(ValidationException)
	}

	def "Apply conversion subrule to invalid term"() {
		setup:
		  def converterTerm = createToIntConverter()
		  def subrule = SubrulesFactory.create(converterTerm, new ValidationErrorProperties())
		when:
		  subrule.apply(INVALID_PARAMETER_VALUE)
		then:
		  thrown(ValidationException)
	}

	def "create for term"() {
		setup:
		  def subrule = SubrulesFactory.create(createValidationTerm())
		expect:
		  subrule.term instanceof Term
	}
		
	def "create for closure"() {
		setup:
		  def closure = {}
		  def subrule = SubrulesFactory.create(closure)
		expect:
		  (subrule.term as ClosureTerm).closure == closure
	}
	
	def "create for null"() {
		when:
		  SubrulesFactory.create(null)
		then:
		  thrown(InvalidSubruleException)
	}
	
	def "create for invalid subrule"() {
		when:
		  SubrulesFactory.create(0)
		then:
		  thrown(InvalidSubruleException)
	}
}