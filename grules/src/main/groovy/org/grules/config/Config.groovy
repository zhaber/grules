package org.grules.config

/**
 * A common ancestor for configuration classes.
 */
abstract class Config {

  /**
   * Returns a map with configuration properties.
   */
  protected abstract Map<String, Object> getParameters()

  /**
   * Returns all configuration parameters as a string.
   */
  @Override
  String toString() {
    parameters.toString()
  }
}