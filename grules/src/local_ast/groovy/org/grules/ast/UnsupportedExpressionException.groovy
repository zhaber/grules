package org.grules.ast

import groovy.transform.InheritConstructors

/**
 * Signals an invalid rule expression.
 */
@InheritConstructors
class UnsupportedExpressionException extends Exception {

  UnsupportedExpressionException(Class clazz) {
    super(clazz.name)
  }

}
