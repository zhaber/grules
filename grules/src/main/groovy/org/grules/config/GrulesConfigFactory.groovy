package org.grules.config

/**
 * The factory creates a grules configuration object from a groovy file or class.
 */
class GrulesConfigFactory implements ConfigFactory {

  /**
   * Creates a grules configuration based on the specified script file.
   */
  @Override
  GrulesConfig createConfig(Class<? extends Script> configClass) {
    new GrulesConfig(ConfigObjectFactory.createConfigObject(configClass, createDefaultConfig()))
  }

  /**
   * Creates a grules configuration based on the script file.
   */
  @Override
  GrulesConfig createConfig() {
    new GrulesConfig(ConfigObjectFactory.createConfigObject(createDefaultConfig()))
  }

  /**
   * Creates default grules configuration object.
   */
  @Override
  GrulesConfig createDefaultConfig() {
    DefaultGrulesConfig.INSTANCE
  }
}

