package org.grules.script

import org.grules.GrulesException

import groovy.transform.InheritConstructors

/**
 * Signals that a required parameter was not supplied.
 */
@InheritConstructors
class MissingParameterException extends GrulesException {

  final String group
  final String parameterName

  MissingParameterException(String group, String parameterName) {
    this.group = group
    this.parameterName = parameterName
  }
}
