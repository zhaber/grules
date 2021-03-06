package org.grules.script.expressions

import org.codehaus.groovy.syntax.Types
import org.grules.ValidationErrorProperties

/**
 * Operators used in rule expressions: and, or, chaining operator (<code>>></code>), binding an error (<code>[]</code>)
 */
interface Operators {

  @Category(Term)
  class TermOperators {

    /**
     * Binds an error id to a subrule.
     */
    Subrule getAt(errorId) {
      SubruleFactory.create(this, new ValidationErrorProperties(errorId))
    }

    /**
     * Binds an error id to a subrule. This method is needed to override native "get property" Groovy method.
     */
    Subrule getAt(String errorId) {
      SubruleFactory.create(this, new ValidationErrorProperties(errorId))
    }

    /**
     * Binds a error properties to a current subrule.
     */
    Subrule getAt(ValidationErrorProperties errorProperties) {
      SubruleFactory.create(this, errorProperties)
    }

    /**
     * Creates a validation term that is a conjunction of two other terms.
     */
    Term and(expression) {
      Term term = TermWrapper.wrap(expression)
      new AndTerm(this, term)
    }

    /**
     * This method is added to produce an exception when a conjunction is applied to a conversion term.
     */
    Term and(TildeTerm expression) {
      throw new InvalidBooleanTermException(this, expression, Types.LOGICAL_AND)
    }

    /**
     * Creates a validation term that is a disjunction of two other terms.
     */
    Term or(expression) {
      Term term = TermWrapper.wrap(expression)
      new OrTerm(this, term)
    }

    /**
     * This method is added to produce an exception when a disjunction is applied to a conversion term.
     */
    Term or(TildeTerm expression) {
      throw new InvalidBooleanTermException(this, expression, Types.LOGICAL_OR)
    }

    /**
     * Creates a validation term that is a negation of itself.
     */
    Term negative() {
      new NotTerm(this)
    }

    /**
     * Creates a conversion term.
     */
    Term bitwiseNegate() {
      new TildeTerm(this)
    }
  }

  @Category(TildeTerm)
  class TildeTermOperators {

    /**
     * This method is added to produce an exception when a conjunction is applied to a conversion term.
     */
    Term and(expression) {
      throw new InvalidBooleanTermException(expression)
    }

    /**
     * This method is added to produce an exception when a disjunction is applied to a conversion term.
     */
    Term or(expression) {
      throw new InvalidBooleanTermException(expression)
    }

    /**
     * This method is added to produce an exception when a negation is applied to a conversion term.
     */
    Term negative() {
      throw new InvalidBooleanTermException(this, Types.NOT)
    }
  }

  @Category(Closure)
  class ClosureOperators {

    /**
     * Creates a validation term that is a conjunction of validation closure and the given term.
     */
    Term and(rightTerm) {
      (new ClosureTerm(this) as Term) & rightTerm
    }

    /**
     * Creates a validation term that is a disjunction of validation closure and the given term.
     */
    Term or(rightTerm) {
      (new ClosureTerm(this) as Term) | rightTerm
    }

    /**
     * Creates a validation term that is a negation of the given closure.
     */
    Term negative() {
      new NotTerm(this)
    }

    /**
     * Binds an error id to a subrule implemented as a closure.
     */
    Subrule getAt(errorId) {
      SubruleFactory.create(new ClosureTerm(this), new ValidationErrorProperties(errorId))
    }

    /**
     * Binds an error id to a subrule implemented as a closure. This method is needed to override native
     * "get property" Groovy method.
     */
    Subrule getAt(String errorId) {
      SubruleFactory.create(new ClosureTerm(this), new ValidationErrorProperties(errorId))
    }

    /**
     * Binds a redirect URL to a subrule implemented as a closure.
     */
    Subrule getAt(ValidationErrorProperties errorProperties) {
      SubruleFactory.create(new ClosureTerm(this), errorProperties)
    }

    /**
     * Creates a conversion term.
     */
    Term bitwiseNegate() {
      new TildeTerm(this)
    }

  }

  /**
   * <code>>></code> operator that combines two subrules in a sequence such that a result of application of a first
   * subrule is passed to a second subrule.
   */
  @Category(SubrulesSeq)
  class SubrulesSeqOperators {

    SubrulesSeq rightShift(exression) {
      (this as SubrulesSeq).add(exression)
    }
  }
}
