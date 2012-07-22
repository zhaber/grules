package org.grules

import java.util.logging.ConsoleHandler
import java.util.logging.Formatter
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

import org.grules.config.Config

/**
 * A common logger for all rule engine events.
 */
class GrulesLogger {

  private static final Logger LOGGER = Logger.getLogger('grules')
  private static final Config CONFIG = GrulesInjector.config

  static {
    LOGGER.level = CONFIG.logLevel
    LOGGER.addHandler(CONFIG.loggerHandler)
  }

  static void fine(String message) {
    LOGGER.fine(message)
  }

  static void warn(String message) {
    LOGGER.warning(message)
  }

  static void turnOff() {
    LOGGER.level = Level.OFF
  }
}

/**
* A console handler for the logger.
*/
class StdoutConsoleHandler extends ConsoleHandler {

  @Override
  protected void setOutputStream(OutputStream out) {
    super.setOutputStream(System.out)
    level = Level.FINE
    formatter = new Formatter() {
       String format(LogRecord record) {
        record.message + '\n'
      }
    }
  }
}
