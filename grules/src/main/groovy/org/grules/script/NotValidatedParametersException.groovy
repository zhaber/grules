package org.grules.script

import org.grules.GrulesException

/**
 * Signals that to some parameters preprocessing rules were not applied.
 */
abstract class NotValidatedParametersException extends GrulesException {

  abstract getParameters()

  protected NotValidatedParametersException(String parameters) {
    super('No rule for parameters ' + parameters)
  }
}

/**
 * NotValidatedParametersException for a rules script with several groups.
 */
class NotValidatedParametersGroupException extends NotValidatedParametersException {

  final Map<String, Map<String, String>> parameters

  NotValidatedParametersGroupException(Map<String, Map<String, String>> parameters) {
    super(parameters.toString())
    this.parameters = parameters
  }
}

/**
 * NotValidatedParametersException for rules script with one default group.
 */
class NotValidatedParametersFlatException extends NotValidatedParametersException {

  final Map<String, String> parameters

  NotValidatedParametersFlatException(Map<String, String> parameters) {
    super(parameters.toString())
    this.parameters = parameters
  }
}
