package org.grules.script

import static org.grules.TestScriptEntities.*

import org.grules.EmptyRulesScript

import spock.lang.Specification

class RulesScriptFactoryTest extends Specification {
	
	def "Created scripts are of a right class"() {
		setup:
			def script = (new RulesScriptFactory()).newInstanceMain(EmptyRulesScript, [:], [:])
	  when:
		  script as EmptyRulesScript
		then:
		  notThrown(ClassCastException)
		expect:
			script instanceof RulesScript
	}
}