package org.grules.config

import java.util.logging.FileHandler
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.Formatter
import java.util.logging.LogRecord
import java.util.logging.StreamHandler

import org.grules.http.HttpRequestParametersGroup

/**
 * Default values of grules configuration properties.
 */
class DefaultGrulesConfig extends GrulesConfig {
  static final GrulesConfig INSTANCE = new DefaultGrulesConfig()

  static Handler createLogHandler() {
    def defaultHandler = new StreamHandler()
    try {
      Handler handler = new FileHandler('grules.log')
      handler.setFormatter(new Formatter() {
        String format(LogRecord record) {
          record.level.name + ': ' + record.message + '\n'
        }
      } )
      handler
    } catch (IOException e) {
      defaultHandler
    } catch (GroovyRuntimeException e) {
      // Google App Engine security exception
      defaultHandler
    } catch (NoClassDefFoundError e) {
      // Google App Engine restricted class exception
      defaultHandler
    }
  }

  DefaultGrulesConfig() {
    super([(GrulesConfig.NOT_VALIDATED_PARAMETERS_ACTION_PARAMETER_NAME):OnValidationEventAction.IGNORE,
    (GrulesConfig.DEFAULT_GROUP_PARAMETER_NAME):HttpRequestParametersGroup.PARAMETERS.name(),
    (GrulesConfig.LOG_LEVEL_PARAMETER_NAME):Level.FINE,
    (GrulesConfig.ENABLE_MULTITHREADING_PARAMETER_NAME):false,
    (GrulesConfig.GROUPS_PARAMETER_NAME):HttpRequestParametersGroup.values()*.name(),
    (GrulesConfig.LOGGER_HANDLER_PARAMETER_NAME):createLogHandler(),
    (GrulesConfig.RESOURCE_BUNDLE_PARAMETER_NAME):'messages',
    (GrulesConfig.DEFAULT_FUNCTIONS_PARAMETER_NAME):[]])
  }
}

