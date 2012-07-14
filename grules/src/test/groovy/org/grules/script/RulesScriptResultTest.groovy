package org.grules.script

import static org.grules.TestRuleEntriesFactory.*
import static org.grules.TestScriptEntities.*
import spock.lang.Specification

class RulesScriptResultTest extends Specification {
	
	RulesScript script
	Map<String, Map<String, Object>> cleanParameters
	Map<String, Set<String>> notValidatedParameters
	Map<String, Set<String>> missingRequiredParameters
	Map<String, Map<String, Map<String, Object>>> invalidParameters
	Map<String, Set<String>> parametersWithMissingDependency
		
	def setup() {
		cleanParameters = [(GROUP): [(PARAMETER_NAME): PARAMETER_VALUE]]
		notValidatedParameters = [(GROUP): [(PARAMETER_NAME_AUX): PARAMETER_VALUE]]
		missingRequiredParameters = [(GROUP): [PARAMETER_NAME_AUX] as Set]
		invalidParameters = [(GROUP): [(PARAMETER_NAME_AUX): [:]]]
		parametersWithMissingDependency = [(GROUP): [PARAMETER_NAME_AUX] as Set]
		script = Mock()
		script.fetchCleanParameters() >> cleanParameters
		script.fetchNotValidatedParameters() >> notValidatedParameters 
		script.missingRequiredParameters >> missingRequiredParameters 
		script.invalidParameters >> invalidParameters
		script.parametersWithMissingDependency >> parametersWithMissingDependency 
	}

	def "fetch gets all GROUPed variables"() {
		setup:
		  RulesScriptGroupResult scriptResult = RulesScriptResultFetcher.fetchGroupResult(script, cleanParameters)
		expect:
		  scriptResult.cleanParameters == cleanParameters
		  scriptResult.notValidatedParameters == notValidatedParameters
			scriptResult.missingRequiredParameters == missingRequiredParameters
			scriptResult.invalidParameters == invalidParameters
			scriptResult.parametersWithMissingDependency == parametersWithMissingDependency
	}
	
	def "fetchFlat method gets not GROUPed variables"() {
		setup:
			RulesScriptResult scriptResult = RulesScriptResultFetcher.fetchResult(script, GROUP, cleanParameters.get(GROUP))
		expect:
		  scriptResult.cleanParameters == cleanParameters[GROUP]
		  scriptResult.notValidatedParameters == notValidatedParameters[GROUP]
			scriptResult.missingRequiredParameters == missingRequiredParameters[GROUP]
			scriptResult.invalidParameters == invalidParameters[GROUP]
			scriptResult.parametersWithMissingDependency == parametersWithMissingDependency[GROUP]
	}
}