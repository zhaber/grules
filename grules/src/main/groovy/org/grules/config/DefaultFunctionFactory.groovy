package org.grules.config

import org.codehaus.groovy.runtime.MethodClosure
import org.grules.script.expressions.ClosureTerm
import org.grules.script.expressions.FunctionTerm
import org.grules.script.expressions.Subrule
import org.grules.script.expressions.SubruleFactory

/**
 * Creates default grules functions (<code>trim</code> etc).
 */
class DefaultFunctionFactory {

  /**
   * Creates a default function based on the given method closure and an error message.
   *
   * @param methodClosure a validation or conversion method closure
   * @param errorMessage an error message
   * @return a subrule
   */
  static Subrule create(MethodClosure methodClosure, String errorMessage) {
    SubruleFactory.create(new FunctionTerm(methodClosure), errorMessage)
  }

  /**
   * Creates a default function based on the given method closure.
   *
   * @param methodClosure a validation or conversion method closure
   * @param errorMessage an error message
   * @return a subrule
   */
  static Subrule create(MethodClosure methodClosure) {
    SubruleFactory.create(new FunctionTerm(methodClosure))
  }

  /**
   * Creates a default function based on the given closure and function name.
   *
   * @param closure a validation or conversion closure
   * @param name a function name
   * @return a subrule
   */
  static Subrule create(Closure closure, String name) {
    SubruleFactory.create(new FunctionTerm(closure, name))
  }

  /**
   * Creates a default function based on the given closure.
   *
   * @param closure a validation or conversion closure
   * @return a subrule
   */
  static Subrule create(Closure closure) {
    SubruleFactory.create(new ClosureTerm(closure))
  }

  /**
   * Creates a default function based on the given closure, function name, and error message
   *
   * @param closure a validation or conversion closure
   * @param name a function name
   * @param errorMessage an error message
   * @return a subrule
   */
  static Subrule create(Closure closure, String name, String errorMessage) {
    SubruleFactory.create(new FunctionTerm(closure, name, errorMessage))
  }
}

