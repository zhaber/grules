package org.grules.script

import org.grules.EmptyRulesScriptGrules

import spock.lang.Specification

class RulesScriptFactoryTest extends Specification {

  def "Created scripts are of a right class"() {
    setup:
      def script = (new RulesScriptFactory()).newInstanceMain(EmptyRulesScriptGrules, [:], [:])
      when:
      script as EmptyRulesScriptGrules
    then:
      notThrown(ClassCastException)
    expect:
      script instanceof RulesScript
  }
}
