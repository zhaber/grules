package org.grules.config

/**
 * A factory that produces a configuration object used by a compiler during AST transformation.
 */
class AstTransformationConfigFactory implements ConfigFactory {

  /** {@inheritDoc} */
  @Override
  AstTransformationConfig createConfig(Class<? extends Script> configClass) {
    new AstTransformationConfig(ConfigObjectFactory.createConfigObject(configClass, createDefaultConfig()))
  }

  /** {@inheritDoc} */
  @Override
  AstTransformationConfig createConfig() {
    new AstTransformationConfig(ConfigObjectFactory.createConfigObject(createDefaultConfig()))
  }

  /** {@inheritDoc} */
  @Override
  AstTransformationConfig createDefaultConfig() {
    DefaultAstTransformationConfig.INSTANCE
  }
}