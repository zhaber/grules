package org.grules

import org.grules.config.GrulesConfig
import org.grules.config.GrulesConfigFactory

import spock.lang.Specification

class MessagesResourceBundleTest extends Specification {

  def grulesConfig = new GrulesConfig([(GrulesConfig.RESOURCE_BUNDLE_PARAMETER_NAME): ''])

  def "Resource bundle is read if exists"() {
    setup:
      def resourceBundle = new MessagesResourceBundle(new GrulesConfigFactory().createConfig())
    expect:
      resourceBundle.messages[TestScriptEntities.ERROR_MSG] == TestScriptEntities.ERROR_MSG
  }

  def "MissingResourceException is not thrown if messages are not read from resource bundle"() {
    when:
      new MessagesResourceBundle(grulesConfig)
    then:
      notThrown(MissingResourceException)
  }

  def "MissingResourceException is thrown on message read"() {
    setup:
      def resourceBundle = new MessagesResourceBundle(grulesConfig)
    when:
      resourceBundle.messages.get('')
    then:
      thrown(MissingResourceException)
  }
}
