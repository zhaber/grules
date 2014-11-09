package org.grules.script.expressions

/**
 * Skip function can be used on a per rule basis
 * and indicates which default converters and validators have to be skipped for a
 * certain parameter. As an example, the following rule says that the trim function
 * need not be applied for the parameter message:
 * <code>message skip(‘trim’) >> sizeLessThan(1000)</code>
 * Skip function must always lead subrules and not be preceded by converters or
 * validators, otherwise a runtime error will be thrown.
 */
class Skip {

  final List<String> functions

  Skip(String[] functions) {
    this.functions = functions as List
  }
}

