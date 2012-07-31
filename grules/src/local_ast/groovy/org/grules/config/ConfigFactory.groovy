package org.grules.config

/**
 * The common interface for configuration factories.
 */
interface ConfigFactory {

  /**
   * Creates a configuration based on the provided script file.
   */
  Config createConfig(Class<? extends Script> configClass)

  /**
   * Creates a configuration based on the script file located at the given path.
   */
  Config createConfig()

  /**
   * Creates default configuration not overridden by custom properties.
   */
  Config createDefaultConfig()
}