package org.grules.script.expressions

import groovy.transform.InheritConstructors

import org.grules.GrulesException

/**
 * Signals an invalid term expression.
 */
@InheritConstructors
class InvalidTermException extends GrulesException {

  InvalidTermException(term) {
    super("Term '$term' is invalid")
  }
}
