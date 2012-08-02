package org.grules.script

import groovy.transform.InheritConstructors

import org.grules.GrulesException

/**
 * Signals that value on which a rule depends is invalid.
 */
@InheritConstructors
class InvalidDependencyValueException extends GrulesException {
}
