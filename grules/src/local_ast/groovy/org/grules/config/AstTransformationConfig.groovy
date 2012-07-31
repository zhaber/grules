package org.grules.config

class AstTransformationConfig extends Config {

  private final Map<String, Object> parameters

  static final String COMPILER_LOG_PATH_PARAMETER_NAME = 'compilerLogPath'

  AstTransformationConfig(Map<String, Object> properties) {
    this.parameters = properties
  }

  @Override
  Map<String, Object> getParameters() {
    parameters
  }

  String getCompilerLogPath() {
    parameters[COMPILER_LOG_PATH_PARAMETER_NAME]
  }
}
