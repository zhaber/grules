package org.grules.script.expressions

/**
 * A term implemented as a function call.
 */
class FunctionTerm implements Term {

  private final Closure closure
  private final String name

  FunctionTerm(Closure closure, String name) {
    this.closure = closure
    this.name = name
  }

  @Override
  def apply(value) {
    closure.call(value)
  }

  @Override
  String toString() {
    name
  }
}
