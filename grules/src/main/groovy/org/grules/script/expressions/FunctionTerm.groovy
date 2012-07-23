package org.grules.script.expressions

import org.codehaus.groovy.runtime.MethodClosure

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

  FunctionTerm(MethodClosure methodClosure) {
    this.closure = methodClosure
    this.name = methodClosure.method
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