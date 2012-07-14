package org.grules

import org.grules.script.RulesScript
import org.grules.script.RulesScriptFactory

class TestScriptFactory {

	static RulesScript newEmptyRulesScript() {
	  (new RulesScriptFactory()).newInstanceMain(EmptyRulesScript, [:])
	}
}