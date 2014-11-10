package org.grules

import org.grules.config.GrulesConfig
import org.grules.config.GrulesConfigFactory

import spock.lang.Specification

class MessageResourceBundleTest extends Specification {

  private final grulesConfig = new GrulesConfig([(GrulesConfig.RESOURCE_BUNDLE_PARAMETER_NAME):''])

  def "Resource bundle is read if exists"() {
    setup:
      def resourceBundle = new MessageResourceBundle(new GrulesConfigFactory().createConfig())
    expect:
      resourceBundle.messages[TestScriptEntities.ERROR_ID] == TestScriptEntities.ERROR_ID
  }

  def "MissingResourceException is not thrown if messages are not read from resource bundle"() {
    when:
      new MessageResourceBundle(grulesConfig)
    then:
      notThrown(MissingResourceException)
  }

  def "MissingResourceException is thrown on message read"() {
    setup:
      def resourceBundle = new MessageResourceBundle(grulesConfig)
    when:
      resourceBundle.messages.get('')
    then:
      thrown(MissingResourceException)
  }
}
