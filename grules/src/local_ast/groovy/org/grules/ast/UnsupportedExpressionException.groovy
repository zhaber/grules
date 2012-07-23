package org.grules.ast

import groovy.transform.InheritConstructors
import org.grules.GrulesException

/**
 * Signals an invalid rule expression.
 */
@InheritConstructors
class UnsupportedExpressionException extends GrulesException {

  UnsupportedExpressionException(Class clazz) {
    super(clazz.name)
  }

}