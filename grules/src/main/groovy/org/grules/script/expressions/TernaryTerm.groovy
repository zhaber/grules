package org.grules.script.expressions


/**
 * A term implemented as a ternary operator.
 */
class TernaryTerm implements Term {

  private final Closure<Boolean> condition
  private final Closure trueBranch
  private final Closure falseBranch

  /**
   * Creates a ternary term.
   * 
   * @param condition condition closure which result determines which branch to choose
   * @param trueBranch closure called if the condition coerces to true
   * @param falseBranch closure called if the condition coerces to false
   */
  TernaryTerm(Closure<Boolean> condition, Closure trueBranch, Closure falseBranch) {
    this.condition = condition
    this.trueBranch = trueBranch
    this.falseBranch = falseBranch
  }

  /** {@inheritDoc} */
  @Override
  def apply(value) {
    condition.call(value) ? trueBranch.call(value) : falseBranch.call(value)
  }
}