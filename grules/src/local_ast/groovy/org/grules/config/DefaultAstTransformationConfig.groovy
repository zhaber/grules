package org.grules.config

class DefaultAstTransformationConfig extends AstTransformationConfig {

  static final AstTransformationConfig INSTANCE = new DefaultAstTransformationConfig()

  DefaultAstTransformationConfig() {
     super([(AstTransformationConfig.COMPILER_LOG_PATH_PARAMETER_NAME): '/tmp/grules'])
  }
}
