package org.grules.config

/**
 * A class with default values of configuration properties used by a compiler during AST transformation.
 */
class DefaultAstTransformationConfig extends AstTransformationConfig {

  static final AstTransformationConfig INSTANCE = new DefaultAstTransformationConfig()

  DefaultAstTransformationConfig() {
     super([(AstTransformationConfig.COMPILER_LOG_PATH_PARAMETER_NAME): '/tmp/grules'])
  }
}
