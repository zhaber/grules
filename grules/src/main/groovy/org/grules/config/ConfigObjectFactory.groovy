package org.grules.config


/**
 * The factory creates a configuration object from a groovy file or class.
 */
class ConfigObjectFactory {

  static final String CONFIG_PATH = '/grules.config'

  /**
   * Creates a configuration object based on the provided script file.
   */
  static Map<String, Object> createConfigObject(Class<? extends Script> configClass, Config defaultConfig) {
    ConfigSlurper configSlurper = new ConfigSlurper()
    try {
      ConfigObject config = new ConfigObject(defaultConfig.parameters)
      ConfigObject customConfig = configSlurper.parse(configClass)
      config.merge(customConfig)
      config
    } catch (SecurityException e) {
      // Workaround for GROOVY-4950
      defaultConfig.parameters
    }
  }

  /**
   * Creates a configuration object based on the script file located at the given path.
   */
  static Map<String, Object> createConfigObject(Config defaultConfig) {
    String filePath = System.getProperty('org.grules.config.file')
    filePath = filePath ?: getResource(CONFIG_PATH)?.path
    if (filePath) {
      GroovyScriptEngine loader = new GroovyScriptEngine('')
      Class configClass = loader.loadScriptByName(filePath)
      createConfigObject(configClass, defaultConfig)
    } else {
      new ConfigObject(defaultConfig.parameters)
    }
  }
}