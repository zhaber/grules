package org.grules.script.expressions

import org.codehaus.groovy.reflection.MixinInMetaClass
import org.codehaus.groovy.runtime.HandleMetaClass
import org.grules.ValidationErrorProperties
import org.grules.ValidationException
import org.grules.functions.ConverterBooleanResult
import org.grules.utils.ClassUtils;

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
    if (applicationResult instanceof Boolean) {
      if (ClassUtils.hasMixin(applicationResult, ConverterBooleanResult) || term instanceof TildeTerm) {
        applicationResult
      } else {
        if (!applicationResult) {
          throw new ValidationException()
        }
        value
      }
    } else {
      applicationResult
    }
  }
}