package org.grules

import org.grules.functions.lib.CommonFunctions
import org.grules.script.expressions.ClosureTerm
import org.grules.script.expressions.Subrule
import org.grules.script.expressions.SubruleFactory
import org.grules.script.expressions.SubrulesSeq
import org.grules.script.expressions.Term
import org.grules.script.expressions.TildeTerm

/**
 * Factory for rule entries used in tests.
 */
class TestRuleEntriesFactory {

  static Closure<SubrulesSeq> createEmptyRuleClosure() {
    { -> new SubrulesSeq() }
  }

  static Term createTerm() {
    createConversionTerm()
  }

  static Term createConversionTerm() {
    createNopTerm()
  }

  static Term createNopTerm() {
    CommonFunctions commonFunctions = new CommonFunctions()
    commonFunctions.&nop as Term
  }

  static Term createValidationTerm() {
    { value -> true } as Term
  }

  static Term createFailValidationTerm() {
    { value -> false } as Term
  }

  static Term createSuccessValidationTerm() {
    { value -> true } as Term
  }

  static TildeTerm createTildeTerm() {
    new TildeTerm(createTerm())
  }

  static ClosureTerm createIsIntegerValidator() {
    new ClosureTerm( { String number -> number.isInteger() })
  }

  static ClosureTerm createIsEmptyValidator() {
    new ClosureTerm( { String term -> term.isEmpty() })
  }

  static ClosureTerm createValidatorClosureTerm() {
    new ClosureTerm( { } )
  }

  static TildeTerm createTrimConverter() {
    new TildeTerm( { String string -> string.trim() } )
  }

  static TildeTerm createToIntConverter() {
    new TildeTerm( { String string ->
      try {
        string.toInteger()
      } catch(NumberFormatException e) {
        throw new ValidationException()
      }
    })
  }

  static SubrulesSeq createSubrulesSeq() {
    (new SubrulesSeq()).add(createTrimConverter())
  }

  static Subrule createSubrule() {
    SubruleFactory.create(createTrimConverter())
  }

  static createFailingSubrule(ValidationErrorProperties errorProperties) {
    new Subrule( { } as Term, errorProperties) {
      @Override
      def apply(value) {
        throw new ValidationException()
      }
    }
  }

  static createFailingSubrule() {
    createFailingSubrule(new ValidationErrorProperties())
  }
}
