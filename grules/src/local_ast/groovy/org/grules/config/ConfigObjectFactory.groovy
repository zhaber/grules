package org.grules.config


/**
 * The factory creates a configuration object from a groovy file or class.
 */
class ConfigObjectFactory {

  static final String CONFIG_PATH = '/grules.config'

  /**
   * Creates a configuration object based on the provided script file.
   */
  static ConfigObject createConfigObject(Class<? extends Script> configClass, Config defaultConfig) {
    ConfigSlurper configSlurper = new ConfigSlurper()
    ConfigObject config = new ConfigObject(defaultConfig.parameters)
    ConfigObject customConfig = configSlurper.parse(configClass)
    config.merge(customConfig)
    config
  }

  /**
   * Creates a configuration object based on the script file located at the given path.
   */
  static ConfigObject createConfigObject(Config defaultConfig) {
    if (getResource(CONFIG_PATH) != null) {
      GroovyScriptEngine loader = new GroovyScriptEngine('')
      Class configClass = loader.loadScriptByName(getResource(CONFIG_PATH).path)
      createConfigObject(configClass, defaultConfig)
    } else {
      new ConfigObject(defaultConfig.parameters)
    }
  }
}