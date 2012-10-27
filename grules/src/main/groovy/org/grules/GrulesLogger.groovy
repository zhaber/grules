package org.grules

import java.util.logging.Level
import java.util.logging.Logger

/**
 * A common logger for all rule engine events.
 */
class GrulesLogger {

  private static final Logger LOGGER = Logger.getLogger('grules')

  static {
    LOGGER.level = GrulesInjector.config.logLevel
    LOGGER.addHandler(GrulesInjector.config.loggerHandler)
    LOGGER.useParentHandlers = false
  }

  static void fine(message) {
    LOGGER.fine(message.toString())
  }

  static void info(message) {
    LOGGER.info(message.toString())
  }

  static void warn(message) {
    LOGGER.warning(message.toString())
  }

  static void turnOff() {
    LOGGER.level = Level.OFF
  }
}