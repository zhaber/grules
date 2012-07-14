package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.Grules
import org.grules.script.RulesScriptGroupResult

import spock.lang.Specification

class GroupsTest extends Specification {
	
	def "changeGroup changes group"() {
		setup:
		  def parameters = [(GROUP_AUX): [(PARAMETER_NAME): PARAMETER_VALUE]]
			RulesScriptGroupResult result = Grules.applyGroupRules(GroupsGrules, parameters)
		expect:
			result.cleanParameters.get(GROUP_AUX).containsKey(PARAMETER_NAME)
	}
	
}