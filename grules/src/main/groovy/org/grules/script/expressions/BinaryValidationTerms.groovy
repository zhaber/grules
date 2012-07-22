package org.grules.script.expressions

import groovy.transform.InheritConstructors

/**
 * A validation term that combined from two other terms via a boolean operation of arity two, for example disjunction or
 * conjunction.
 */
abstract class BinaryValidationTerm implements Term {

  protected final Term leftTerm
  protected final Term rightTerm

  protected BinaryValidationTerm(Term leftTerm, Term rightTerm) {
    this.leftTerm = leftTerm
    this.rightTerm = rightTerm
  }

  static void checkBooleanReturnValue(value, Term term) {
    if (!(value instanceof Boolean)) {
      if (term instanceof FunctionTerm) {
        throw new InvalidBooleanTermException(value, (term as FunctionTerm).name)
      } else {
        throw new InvalidBooleanTermException(value)
      }
    }
  }

  @Override
  abstract Boolean apply(value)
}

/**
 * A term which is a conjunction of two other terms.
 */
@InheritConstructors
class AndTerm extends BinaryValidationTerm {

  /**
   * Checks if both conditions represented by subterms are valid.
   */
  @Override
  Boolean apply(value) {
    def leftTermApplicationResult = leftTerm.apply(value)
    checkBooleanReturnValue(leftTermApplicationResult, leftTerm)
    if (!leftTermApplicationResult) {
      return false
    }
    def rightTermApplicationResult = rightTerm.apply(value)
    checkBooleanReturnValue(rightTermApplicationResult, rightTerm)
    rightTermApplicationResult
  }
}

/**
 * A term which is a disjunction of two other terms.
 */
@InheritConstructors
class OrTerm extends BinaryValidationTerm {

  /**
   * Checks if at least one condition represented by subterms is valid.
   */
  @Override
  Boolean apply(value) {
    def leftTermApplicationResult = leftTerm.apply(value)
    checkBooleanReturnValue(leftTermApplicationResult, leftTerm)
    if (leftTermApplicationResult) {
      return true
    }
    def rightTermApplicationResult = rightTerm.apply(value)
    checkBooleanReturnValue(rightTermApplicationResult, rightTerm)
    rightTermApplicationResult
  }
}
