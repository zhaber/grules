package org.grules.script

import org.codehaus.groovy.runtime.MethodClosure
import org.grules.Grules
import org.grules.GrulesException
import org.grules.GrulesInjector
import org.grules.config.ConfigFactory

/**
 * Signals that the group is not declared in configuration file.
 */
class InvalidGroupException extends GrulesException {

	InvalidGroupException(String name) {
		super("Group '$name' is not in registered groups: ${GrulesInjector.config.groups}, " +
			     "see ${ConfigFactory.CONFIG_PATH}. Possible solutions: add group to the config file, use " + 
					  (Grules.&applyFlatRules as MethodClosure).method + ' for single group of parameters.')
	}
}