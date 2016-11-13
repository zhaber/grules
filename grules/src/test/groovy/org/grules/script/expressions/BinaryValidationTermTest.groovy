package org.grules.script.expressions

import static org.grules.TestRuleEntriesFactory.createIsIntegerValidator
import static org.grules.TestRuleEntriesFactory.createIsEmptyValidator
import static org.grules.TestScriptEntities.VALID_INTEGER_STRING
import spock.lang.Specification

class BinaryValidationTermTest extends Specification {

  def "Apply AND term to valid terms"() {
    setup:
      def validatorTermLeft = createIsIntegerValidator()
      def validatorTermRight = createIsIntegerValidator()
      def term = new AndTerm(validatorTermLeft, validatorTermRight)
    expect:
      term.apply(VALID_INTEGER_STRING)
  }

  def "Apply AND term to invalid terms"() {
    setup:
      def validatorTermLeft = createIsIntegerValidator()
      def validatorTermRight = createIsEmptyValidator()
      def term = new AndTerm(validatorTermLeft, validatorTermRight)
    expect:
      !term.apply(VALID_INTEGER_STRING)
  }

  def "Apply OR term to valid terms"() {
    setup:
      def validatorTermLeft = createIsIntegerValidator()
      def validatorTermRight = createIsEmptyValidator()
      def term = new OrTerm(validatorTermLeft, validatorTermRight)
    expect:
      term.apply(VALID_INTEGER_STRING)
  }

  def "Apply OR term to invalid terms"() {
    setup:
      def validatorTermLeft = createIsEmptyValidator()
      def validatorTermRight = createIsEmptyValidator()
      def term = new OrTerm(validatorTermLeft, validatorTermRight)
    expect:
      !term.apply(VALID_INTEGER_STRING)
  }
}
