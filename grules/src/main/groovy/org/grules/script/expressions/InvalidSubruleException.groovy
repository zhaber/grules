package org.grules.script.expressions

import org.grules.GrulesException

/**
 * Signals an invalid subrule expression.
 */
class InvalidSubruleException extends GrulesException {

  InvalidSubruleException(subrule) {
    super("Subrule '$subrule' is invalid")
  }
}
