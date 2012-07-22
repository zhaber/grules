package org.grules.script.expressions

import org.codehaus.groovy.syntax.Types

/**
 * A term that consists of an operator and a term to which it has to be applied, for example a negation operator.
 */
abstract class UnaryTerm implements Term {

  abstract Term getTerm()
}

/**
 * A term that represents a negation operator on a validation term.
 */
class NotTerm extends UnaryTerm {

  final Term term

  NotTerm(Term term) {
    this.term = term
  }

  NotTerm(Closure closure) {
    this.term = new ClosureTerm(closure)
  }

  @Override
  Boolean apply(value) {
    def termValue = term.apply(value)
    if (!(termValue instanceof Boolean)) {
      throw new InvalidBooleanTermException(this, Types.NOT)
    }
    !termValue
  }
}

/**
 * A conversion term for boolean expressions.
 */
class TildeTerm extends UnaryTerm {

  final Term term

  TildeTerm(Closure closure) {
    this.term = new ClosureTerm(closure)
  }

  TildeTerm(Term term) {
    this.term = term
  }

  /**
   * Returns a result of closure application.
   */
  @Override
  def apply(value) {
    term.apply(value)
  }
}
