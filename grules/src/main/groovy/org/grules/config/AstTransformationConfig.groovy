package org.grules.config

/**
 * Configuration used by AST transformations.
 */
class AstTransformationConfig extends Config {

  private final Map<String, Object> parameters

  static final String COMPILER_LOG_PATH_PARAMETER_NAME = 'compilerLogPath'

  AstTransformationConfig(Map<String, Object> properties) {
    this.parameters = properties
  }

  /**
   * {@inheritDoc}
   */
  @Override
  Map<String, Object> getParameters() {
    parameters
  }

  /**
   * Path for logs produced by compiler during AST transformations.
   */
  String getCompilerLogPath() {
    parameters[COMPILER_LOG_PATH_PARAMETER_NAME]
  }
}

