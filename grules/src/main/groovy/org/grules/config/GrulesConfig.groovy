package org.grules.config

import java.util.logging.Handler
import java.util.logging.Level

import org.grules.GrulesException
import org.grules.script.expressions.Subrule

/**
 * Grules configuration parameters.
 */
class GrulesConfig extends Config {

  private final Map<String, Object> parameters

  static final String NOT_VALIDATED_PARAMETERS_ACTION_PARAMETER_NAME = 'notValidatedParametersAction'
  static final String DEFAULT_GROUP_PARAMETER_NAME = 'defaultGroup'
  static final String LOG_LEVEL_PARAMETER_NAME = 'logLevel'
  static final String LOGGER_HANDLER_PARAMETER_NAME = 'loggerHandler'
  static final String GROUPS_PARAMETER_NAME = 'groups'
  static final String RESOURCE_BUNDLE_PARAMETER_NAME = 'resourceBundlePath'
  static final String DEFAULT_FUNCTIONS_PARAMETER_NAME = 'defaultFunctions'

  GrulesConfig(Map<String, Object> properties) {
    this.parameters = properties
  }

  /**
   * Group to use when no group is specified.
   */
  String getDefaultGroup() {
    String group = parameters[DEFAULT_GROUP_PARAMETER_NAME]
    if (!(group in groups)) {
      throw new GrulesException("Group $group is not in group list $groups")
    }
    group
  }

  /**
   * Set of available groups.
   */
  Set<String> getGroups() {
    parameters[GROUPS_PARAMETER_NAME] as Set
  }

  /**
   * Level of logging used by the rule engine.
   */
  Level getLogLevel() {
    parameters[LOG_LEVEL_PARAMETER_NAME]
  }

  /**
   * Logger handler used by the rule engine.
   */
  Handler getLoggerHandler() {
    parameters[LOGGER_HANDLER_PARAMETER_NAME]
  }

  /**
   * Action performed when there is no defined rule for some input parameter.
   */
  OnValidationEventAction getNotValidatedParametersAction() {
    parameters[NOT_VALIDATED_PARAMETERS_ACTION_PARAMETER_NAME]
  }

  /**
   * Resource bundle for error messages.
   */
  String getResourceBundlePath() {
    parameters[RESOURCE_BUNDLE_PARAMETER_NAME]
  }

  /**
   * Sequence of converters that must be applied to all parameters.
   */
  List<Subrule> getDefaultFunctions() {
    parameters[DEFAULT_FUNCTIONS_PARAMETER_NAME]
  }

  /** {@inheritDoc} */
  @Override
  Map<String, Object> getParameters() {
    parameters
  }
}