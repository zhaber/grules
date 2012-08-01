package org.grules.script.expressions

import org.grules.ValidationErrorProperties

/**
 * Factory for conversion and validation subrules.
 */
class SubruleFactory {

  /**
   * Creates a subrule based on the given term and error properties.
   *
   * @param term a term
   * @param errorProperties error properties
   * @return a subrule
   */
  static Subrule create(Term term, ValidationErrorProperties errorProperties) {
     new Subrule(term, errorProperties)
  }

  /**
   * Creates a subrule based on the given term and an error id.
   *
   * @param term a term
   * @param errorId an error id
   * @return a subrule
   */
  static Subrule create(Term term, String errorId) {
     new Subrule(term, new ValidationErrorProperties(errorId))
  }


  /**
   * Creates a subrule based on the given term.
   *
   * @param term a conversion or validation term
   * @return a subrule
   */
  static Subrule create(Term term) {
    create(term, new ValidationErrorProperties())
  }

  /**
   * Creates a subrule by wrapping a closure to a validation term.
   *
   * @param closure a validation or conversion closure
   * @return a subrule
   */
  static Subrule create(Closure closure) {
    create(new ClosureTerm(closure))
  }

  /**
   * The method throws an error for unexpected subrule expression type.
   */
  static Subrule create(expression) {
    throw new InvalidSubruleException(expression)
  }
}