package org.grules.config

import java.util.logging.Level

import org.grules.StdoutConsoleHandler
import org.grules.functions.lib.StringFunctions
import org.grules.http.HttpRequestParametersGroup

/**
 * Default values of grules configuration properties.
 */
class DefaultGrulesConfig extends GrulesConfig {
  static final GrulesConfig INSTANCE = new DefaultGrulesConfig()

  DefaultGrulesConfig() {
    super([(GrulesConfig.NOT_VALIDATED_PARAMETERS_ACTION_PARAMETER_NAME): OnValidationEventAction.IGNORE,
    (GrulesConfig.DEFAULT_GROUP_PARAMETER_NAME): HttpRequestParametersGroup.PARAMETERS.name(),
    (GrulesConfig.LOG_LEVEL_PARAMETER_NAME): Level.FINE,
    (GrulesConfig.GROUPS_PARAMETER_NAME): HttpRequestParametersGroup.values()*.name(),
    (GrulesConfig.LOGGER_HANDLER_PARAMETER_NAME): new StdoutConsoleHandler(),
    (GrulesConfig.RESOURCE_BUNDLE_PARAMETER_NAME): 'messages',
    (GrulesConfig.DEFAULT_FUNCTIONS_PARAMETER_NAME): [DefaultFunctionFactory.create(StringFunctions.&trim)]])
  }
}