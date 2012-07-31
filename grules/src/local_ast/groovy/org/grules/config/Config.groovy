package org.grules.config

/**
 * Configuration class.
 */
abstract class Config {
  abstract Map<String, Object> getParameters()

  /** Returns all configuration parameters as a string. */
  @Override
  String toString() {
    parameters.toString()
  }
}
