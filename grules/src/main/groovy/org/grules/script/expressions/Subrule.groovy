package org.grules.script.expressions

import org.grules.ValidationErrorProperties
import org.grules.ValidationException
import org.grules.functions.ConverterBooleanResult

/**
 * A subrule expression (parts of a rule between ">>" operators).
 */
class Subrule {

  private final Term term
  final ValidationErrorProperties errorProperties

  Subrule(Term term, ValidationErrorProperties errorProperties) {
    this.errorProperties = errorProperties
    this.term = term
  }

  /**
   * Applies the subrule to the given value.
   */
  @Override
  def apply(value) {
    def applicationResult = term.apply(value)
    if (applicationResult instanceof Boolean && !(term instanceof TildeTerm)) {
      if (!applicationResult) {
        throw new ValidationException()
      }
      value
    } else if (applicationResult instanceof ConverterBooleanResult) {
      (applicationResult as ConverterBooleanResult).value
    } else {
      applicationResult
    }
  }
}