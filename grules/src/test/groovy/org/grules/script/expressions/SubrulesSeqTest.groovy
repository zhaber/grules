package org.grules.script.expressions

import static org.grules.TestRuleEntriesFactory.createIsIntegerValidator
import static org.grules.TestRuleEntriesFactory.createToIntConverter
import static org.grules.TestRuleEntriesFactory.createSubrule
import static org.grules.TestRuleEntriesFactory.createSubrulesSeq
import static org.grules.TestRuleEntriesFactory.createValidationTerm
import static org.grules.TestRuleEntriesFactory.createFailingSubrule
import static org.grules.TestScriptEntities.VALID_INTEGER_STRING
import static org.grules.TestScriptEntities.VALID_INTEGER
import static org.grules.TestScriptEntities.INVALID_PARAMETER_VALUE
import static org.grules.TestScriptEntities.ERROR_ID
import static org.grules.TestScriptEntities.ERROR_MESSAGE

import org.grules.ValidationErrorProperties
import org.grules.ValidationException

import spock.lang.Specification

class SubrulesSeqTest extends Specification {

	def "Apply subrules sequence from one subrule to valid term"() {
		setup:
		  def subrulesSeq = new SubrulesSeq()
		  subrulesSeq.add(createIsIntegerValidator())
		when:
		  def value = subrulesSeq.apply(VALID_INTEGER_STRING)
		then:
		  notThrown(ValidationException)
		expect:
			value == VALID_INTEGER_STRING
	}

	def "Apply subrules sequence from two subrules to valid term"() {
		setup:
			def subrulesSeq = new SubrulesSeq()
			subrulesSeq.add(createIsIntegerValidator())
			subrulesSeq.add(createToIntConverter())
		when:
			def value = subrulesSeq.apply(VALID_INTEGER_STRING)
		then:
			notThrown(ValidationException)
	  expect:
		  value == VALID_INTEGER
	}

	def "Apply subrule that contains error action"() {
		setup:
			def subrulesSeq = new SubrulesSeq()
			subrulesSeq.add(createFailingSubrule(new ValidationErrorProperties(ERROR_ID)))
		when:
			subrulesSeq.apply(INVALID_PARAMETER_VALUE)
		then:
			ValidationException e = thrown(ValidationException)
		expect:
		  e.errorProperties.errorId == ERROR_ID
	}

	def "saves subrule index"() {
		setup:
			def subrulesSeq = new SubrulesSeq()
			Subrule failingSubrule = createFailingSubrule()
			subrulesSeq.add(createIsIntegerValidator())
			subrulesSeq.add(failingSubrule)
		when:
			subrulesSeq.apply(VALID_INTEGER_STRING)
		then:
			ValidationException e = thrown(ValidationException)
		expect:
			e.errorProperties.subruleIndex == subrulesSeq.subrules.indexOf(failingSubrule) + 1
 	}

	def "Apply subrule that does not contain error action"() {
		setup:
			def subrulesSeq = new SubrulesSeq()
			Subrule failingSubrule = createFailingSubrule()
			subrulesSeq.add(failingSubrule)
		when:
			subrulesSeq.apply(INVALID_PARAMETER_VALUE)
		then:
			ValidationException e = thrown(ValidationException)
		expect:
			!e.errorProperties.hasErrorId()
	}

	def "Apply subrule that does not contain error action but subsequent rule does"() {
		setup:
			def subrulesSeq = new SubrulesSeq()
			Subrule failingSubrule = createFailingSubrule()
			Subrule nextSubrule = createFailingSubrule(new ValidationErrorProperties(ERROR_ID))
			subrulesSeq.add(failingSubrule)
			subrulesSeq.add(nextSubrule)
		when:
			subrulesSeq.apply(INVALID_PARAMETER_VALUE)
		then:
			ValidationException e = thrown(ValidationException)
		expect:
			e.errorProperties.errorId == ERROR_ID
	}

  def "Replaces placeholder with original value"() {
    setup:
      def subrulesSeq = new SubrulesSeq()
      Subrule failingSubrule = createFailingSubrule(new ValidationErrorProperties(ERROR_ID, ERROR_MESSAGE + '_'))
      subrulesSeq.add(createToIntConverter())
      subrulesSeq.add(failingSubrule)
    when:
      subrulesSeq.apply(VALID_INTEGER_STRING)
    then:
      ValidationException e = thrown(ValidationException)
    expect:
      e.errorProperties.message == ERROR_MESSAGE + VALID_INTEGER_STRING
  }

	def "Apply sequence to invalid term"() {
		setup:
		  def subrulesSeq = new SubrulesSeq()
		  subrulesSeq.add(createIsIntegerValidator())
		when:
		  subrulesSeq.apply(INVALID_PARAMETER_VALUE)
		then:
		  thrown(ValidationException)
	}

	def "add method adds new subrule"() {
		setup:
		  def subrulesSeq = createSubrulesSeq()
		  def initialSize = subrulesSeq.subrules.size()
		  subrulesSeq.add { }
		expect:
		  subrulesSeq.subrules.size() == initialSize + 1
	}

	def "add method adds a subrule"() {
		setup:
			def subrule = createSubrule()
			def subruleSeq = createSubrulesSeq()
			subruleSeq.add(subrule)
		expect:
			subruleSeq.subrules.last() == subrule
	}

	def "wrap for subrules sequence"() {
		setup:
			def subruleSeq = SubrulesSeqWrapper.wrap(createSubrulesSeq())
		expect:
			subruleSeq instanceof SubrulesSeq
	}

	def "wrap for term"() {
		setup:
			def subruleSeq = SubrulesSeqWrapper.wrap(createValidationTerm())
		expect:
			subruleSeq instanceof SubrulesSeq
	}

	def "wrap for closure"() {
		setup:
			def closure = { }
			def subruleSeq = SubrulesSeqWrapper.wrap(closure)
		expect:
			(subruleSeq.subrules[0].term as ClosureTerm).closure == closure
	}

	def "wrap for null"() {
		when:
			SubrulesSeqWrapper.wrap(null)
		then:
			thrown(InvalidSubrulesSeqException)
	}

	def "wrap for invalid term"() {
		when:
			SubrulesSeqWrapper.wrap(0)
		then:
			thrown(InvalidSubrulesSeqException)
	}
}
