package org.grules.script

import org.codehaus.groovy.runtime.MethodClosure
import org.grules.GrulesAPI
import org.grules.GrulesException
import org.grules.GrulesInjector
import org.grules.config.ConfigObjectFactory

/**
 * Signals that the group is not declared in configuration file.
 */
class InvalidGroupException extends GrulesException {

  InvalidGroupException(String name) {
    super("Group '$name' is not in registered groups: ${GrulesInjector.config.groups}, " +
           "see ${ConfigObjectFactory.CONFIG_PATH}. Possible solutions: add group to the config file, use " +
            (GrulesAPI.&applyFlatRules as MethodClosure).method + ' for single group of parameters.')
  }
}
