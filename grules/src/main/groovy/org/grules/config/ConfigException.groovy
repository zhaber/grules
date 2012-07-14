package org.grules.config

import groovy.transform.InheritConstructors

/**
 * Signals that some configuration property is invalid.  
 */
@InheritConstructors
class ConfigException extends Exception {
}
