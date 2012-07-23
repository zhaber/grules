package org.grules.script.expressions

import static org.grules.TestRuleEntriesFactory.*
import static org.grules.TestScriptEntities.*

import org.grules.ValidationErrorProperties
import org.grules.ValidationException

import spock.lang.Specification

class SubrulesSeqTest extends Specification{

 Subrule failedSubrule

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

  def "Default converters are applied"() {
    setup:
      def subrulesSeq = new SubrulesSeq()
      subrulesSeq.add(createNopTerm())
      def value = subrulesSeq.apply(' ' + PARAMETER_VALUE)
    expect:
      value == PARAMETER_VALUE.trim()
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

	private static createFailingSubrule(ValidationErrorProperties errorProperties) {
		new Subrule({} as Term, errorProperties) {
			@Override
			def apply(value) {throw new ValidationException()}
		}
	}

	def "Apply subrule that contains error action"() {
		setup:
			def subrulesSeq = new SubrulesSeq()
			Subrule failingSubrule = createFailingSubrule(new ValidationErrorProperties(ERROR_MSG))
			subrulesSeq.add(failingSubrule)
		when:
			subrulesSeq.apply(INVALID_PARAMETER_VALUE)
		then:
			ValidationException e = thrown(ValidationException)
		expect:
		  e.errorProperties.message == ERROR_MSG
	}

	def "saves subrule index"() {
		setup:
			def subrulesSeq = new SubrulesSeq()
			Subrule failingSubrule = createFailingSubrule(new ValidationErrorProperties())
			subrulesSeq.add(createIsIntegerValidator())
			subrulesSeq.add(failingSubrule)
		when:
			subrulesSeq.apply(VALID_INTEGER_STRING)
		then:
			ValidationException e = thrown(ValidationException)
		expect:
			e.errorProperties.subruleIndex == 1
	}

	def "Apply subrule that does not contain error action"() {
		setup:
			def subrulesSeq = new SubrulesSeq()
			Subrule failingSubrule = createFailingSubrule(new ValidationErrorProperties())
			subrulesSeq.add(failingSubrule)
		when:
			subrulesSeq.apply(INVALID_PARAMETER_VALUE)
		then:
			ValidationException e = thrown(ValidationException)
		expect:
			!e.errorProperties.hasMessage()
	}

	def "Apply subrule that does not contain error action but subsequent rule does"() {
		setup:
			def subrulesSeq = new SubrulesSeq()
			Subrule failingSubrule = createFailingSubrule(new ValidationErrorProperties())
			Subrule nextSubrule = createFailingSubrule(new ValidationErrorProperties(ERROR_MSG))
			subrulesSeq.add(failingSubrule)
			subrulesSeq.add(nextSubrule)
		when:
			subrulesSeq.apply(INVALID_PARAMETER_VALUE)
		then:
			ValidationException e = thrown(ValidationException)
		expect:
			e.errorProperties.message == ERROR_MSG
	}

  def "Replaces placeholder with original value"() {
    setup:
      def subrulesSeq = new SubrulesSeq()
      Subrule failingSubrule = createFailingSubrule(new ValidationErrorProperties(ERROR_MSG + '_'))
      subrulesSeq.add(createToIntConverter())
      subrulesSeq.add(failingSubrule)
    when:
      subrulesSeq.apply(VALID_INTEGER_STRING)
    then:
      ValidationException e = thrown(ValidationException)
    expect:
      e.errorProperties.message == ERROR_MSG + VALID_INTEGER_STRING
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
		  subrulesSeq.add {}
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
			def closure = {}
			def subruleSeq = SubrulesSeqWrapper.wrap(closure)
      def defaultConvertersSize = DEFAULT_CONFIG.defaultConverters.size()
		expect:
			(subruleSeq.subrules[defaultConvertersSize].term as ClosureTerm).closure == closure
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