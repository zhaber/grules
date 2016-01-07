package org.grules.script

import groovy.transform.InheritConstructors

import org.grules.GrulesException

/**
 * Signals that the rules script is invalid.
 */
@InheritConstructors
class InvalidScriptException extends GrulesException {
}
