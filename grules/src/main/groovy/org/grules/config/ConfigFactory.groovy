package org.grules.config

import java.util.logging.Level

import org.grules.StdoutConsoleHandler
import org.grules.functions.lib.StringFunctions
import org.grules.http.HttpRequestParametersGroup

/**
 * The factory creates a grules configuration object from a groovy file or class.
 */
class ConfigFactory {

  static final String CONFIG_PATH = '/GrulesConfig.groovy'

  private static final Map<String, Object> DEFATULT_CONFIG = [
    (Config.NOT_VALIDATED_PARAMETERS_ACTION_PARAMETER_NAME): OnValidationEventAction.IGNORE,
    (Config.DEFAULT_GROUP_PARAMETER_NAME): HttpRequestParametersGroup.PARAMETERS.name(),
    (Config.LOG_LEVEL_PARAMETER_NAME): Level.FINE,
    (Config.GROUPS_PARAMETER_NAME): HttpRequestParametersGroup.values()*.name(),
    (Config.LOGGER_HANDLER_PARAMETER_NAME): new StdoutConsoleHandler(),
    (Config.RESOURCE_BUNDLE_PARAMETER_NAME): 'messages',
    (Config.DEFAULT_FUNCTIONS_PARAMETER_NAME): [DefaultFunctionFactory.create(StringFunctions.&trim)]]

  /**
   * Creates a grules configuration object based on the provided script file.
   */
  static Config createConfig(Class<? extends Script> configClass) {
    ConfigSlurper configSlurper = new ConfigSlurper()
    ConfigObject config = new ConfigObject(DEFATULT_CONFIG)
    ConfigObject customConfig = configSlurper.parse(configClass)
    config.merge(customConfig)
    new Config(config)
  }

  /**
   * Creates a grules configuration object based on the script file located at the given path.
   */
  static Config createConfig() {
    if (getResource(CONFIG_PATH) != null) {
      GroovyScriptEngine loader = new GroovyScriptEngine('')
      Class configClass = loader.loadScriptByName(getResource(CONFIG_PATH).path)
      createConfig(configClass)
    } else {
      createDefaultConfig()
    }
  }

  /**
   * Creates a grules configuration object based on default properties values.
   */
  static Config createDefaultConfig() {
    new Config(DEFATULT_CONFIG)
  }
}
