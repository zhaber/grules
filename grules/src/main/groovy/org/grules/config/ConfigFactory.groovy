package org.grules.config

/**
 * The common interface for configuration factories.
 */
interface ConfigFactory {

  /**
   * Creates a configuration based on the specified script class.
   */
  Config createConfig(Class<? extends Script> configClass)

  /**
   * Creates a configuration based on a script file.
   */
  Config createConfig()

  /**
   * Creates default configuration not overridden by custom properties.
   */
  Config createDefaultConfig()
}

