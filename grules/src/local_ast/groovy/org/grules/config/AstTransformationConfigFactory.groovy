package org.grules.config

class AstTransformationConfigFactory implements ConfigFactory {

  @Override
  AstTransformationConfig createConfig(Class<? extends Script> configClass) {
    new AstTransformationConfig(ConfigObjectFactory.createConfigObject(configClass, createDefaultConfig()))
  }

  @Override
  AstTransformationConfig createConfig() {
    new AstTransformationConfig(ConfigObjectFactory.createConfigObject(createDefaultConfig()))
  }

  @Override
  AstTransformationConfig createDefaultConfig() {
    DefaultAstTransformationConfig.INSTANCE
  }

}
