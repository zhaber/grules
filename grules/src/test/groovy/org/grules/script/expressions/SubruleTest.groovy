package org.grules.script.expressions

import static org.grules.TestRuleEntriesFactory.createFailValidationTerm
import static org.grules.TestRuleEntriesFactory.createIsIntegerValidator
import static org.grules.TestRuleEntriesFactory.createValidationTerm
import static org.grules.TestRuleEntriesFactory.createTrimConverter
import static org.grules.TestRuleEntriesFactory.createToIntConverter
import static org.grules.TestScriptEntities.VALID_INTEGER_STRING
import static org.grules.TestScriptEntities.PARAMETER_VALUE
import static org.grules.TestScriptEntities.DEFAULT_VALUE
import static org.grules.TestScriptEntities.INVALID_PARAMETER_VALUE

import org.grules.ValidationErrorProperties
import org.grules.ValidationException

import spock.lang.Specification

class SubruleTest extends Specification {

  def "Apply validation subrule to valid term"() {
    setup:
      def validatorTerm = createIsIntegerValidator()
      def subrule = SubruleFactory.create(validatorTerm)
    when:
      subrule.apply(VALID_INTEGER_STRING)
    then:
      notThrown(ValidationException)
  }

  def "When internal validator fails subrule application throws validation exception"() {
    setup:
      def failValidatorTerm = createFailValidationTerm()
      def subrule = SubruleFactory.create(failValidatorTerm, new ValidationErrorProperties())
    when:
      subrule.apply(PARAMETER_VALUE)
    then:
      thrown(ValidationException)
  }

  def "Apply conversion subrule to valid value"() {
    setup:
      def conversionTerm = createTrimConverter()
      def subrule = SubruleFactory.create(conversionTerm)
    when:
      subrule.apply(DEFAULT_VALUE)
    then:
      notThrown(ValidationException)
  }

  def "Apply conversion subrule to invalid value"() {
    setup:
      def converterTerm = createToIntConverter()
      def subrule = SubruleFactory.create(converterTerm, new ValidationErrorProperties())
    when:
      subrule.apply(INVALID_PARAMETER_VALUE)
    then:
      thrown(ValidationException)
  }

  def "create for term"() {
    setup:
      def subrule = SubruleFactory.create(createValidationTerm())
    expect:
      subrule.term instanceof Term
  }

  def "create for closure"() {
    setup:
      def closure = { }
      def subrule = SubruleFactory.create(closure)
    expect:
      (subrule.term as ClosureTerm).closure == closure
  }

  def "create for null"() {
    when:
      SubruleFactory.create(null)
    then:
      thrown(InvalidSubruleException)
  }

  def "create for invalid subrule"() {
    when:
      SubruleFactory.create(0)
    then:
      thrown(InvalidSubruleException)
  }
}
