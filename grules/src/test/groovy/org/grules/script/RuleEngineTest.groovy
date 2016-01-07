package org.grules.script

import static org.grules.TestRuleEntriesFactory.createEmptyRuleClosure
import static org.grules.TestRuleEntriesFactory.createValidatorClosureTerm
import static org.grules.TestRuleEntriesFactory.createValidationTerm
import static org.grules.TestScriptEntities.TEST_CONFIG
import static org.grules.TestScriptEntities.PARAMETER_NAME
import static org.grules.TestScriptEntities.PARAMETER_VALUE
import static org.grules.TestScriptEntities.GROUP

import org.grules.EmptyRulesScriptGrules
import org.grules.GrulesAPI
import org.grules.GrulesInjector
import org.grules.config.GrulesConfig
import org.grules.config.OnValidationEventAction

import spock.lang.Specification

class TestRulesScriptGrules extends Script {
	def run() {
		(this as RulesScript).applyRule(PARAMETER_NAME, PARAMETER_VALUE, createEmptyRuleClosure())
	}
}

class RuleEngineTest extends Specification {

	private RulesScript rulesScript

	def setup() {
		rulesScript = Mock()
	}

	RuleEngine createRuleEngineWithValidationErrorAction() {
		GrulesConfig config = Mock()
		config.getNotValidatedParametersAction() >> OnValidationEventAction.ERROR
    config.getGroups() >> TEST_CONFIG.groups
    config.getDefaultGroup() >> TEST_CONFIG.getDefaultGroup()
    config.isMultithreadingEnabled() >> false
		new RuleEngine(config, new RulesScriptFactory())
	}

	def runMainScript() {
		RuleEngine ruleEngine = new RuleEngine(GrulesInjector.config, new RulesScriptFactory() {
			@Override
			RulesScript newInstanceMain(Class<? extends Script> scriptClass, Map<String, Map<String, Object>> parameters,
		      Map<String, Object> environment) {
				rulesScript
			}
		} )
		ruleEngine.runMainScript(TestRulesScriptGrules, [:], [:])
	}

	def "Exception is thrown on illegal script"() {
		setup:
    	    RuleEngine ruleEngine = new RuleEngine(GrulesInjector.config, new RulesScriptFactory() {
	    		@Override
		    	RulesScript newInstanceMain(Class<? extends Script> scriptClass, Map<String, Map<String, Object>> parameters,
			        Map<String, Object> environment) {
			    	rulesScript
		    	}
		    } )
		when:
		  ruleEngine.runMainScript(Script, [:], [:])
		then:
		  thrown(InvalidScriptException)
	}

	def "Preprocessing of grouped parameters"() {
		setup:
		  def parameters = [(GROUP):[(PARAMETER_NAME):PARAMETER_VALUE]]
			RulesScriptGroupResult result = GrulesAPI.applyGroupRules(TestRulesScriptGrules, parameters)
		expect:
			result.cleanParameters.containsKey(GROUP)
      result.cleanParameters[GROUP] == [(PARAMETER_NAME):PARAMETER_VALUE]
      result.invalidParameters.isEmpty()
	}

	def "Preprocessing of one group"() {
		setup:
			RulesScriptResult result = GrulesAPI.applyRules(TestRulesScriptGrules, [(PARAMETER_NAME):PARAMETER_VALUE])
		expect:
			result.cleanParameters == [(PARAMETER_NAME):PARAMETER_VALUE]
	}

	def "NotValidatedParametersException is thrown when there is parameter without rule (for non-grouped parameters)"() {
		setup:
			def preprocessor = createRuleEngineWithValidationErrorAction().newExecutor(EmptyRulesScriptGrules, [:])
		when:
	     	preprocessor((PARAMETER_NAME):PARAMETER_VALUE)
		then:
		  NotValidatedParametersFlatException e = thrown(NotValidatedParametersException)
			e.parameters == [(PARAMETER_NAME):PARAMETER_VALUE]
	}

	def "NotValidatedParametersException is thrown when there is parameter without rule (for grouped parameters)"() {
		setup:
			def preprocessor = createRuleEngineWithValidationErrorAction().newGroupExecutor(EmptyRulesScriptGrules, [:])
		when:
			preprocessor((GROUP):[(PARAMETER_NAME):PARAMETER_VALUE])
		then:
			NotValidatedParametersGroupException e = thrown(NotValidatedParametersException)
			e.parameters.containsKey(GROUP)
	}

	def "runMainScript runs the scriptClass"() {
		when:
  	      runMainScript()
		then:
		  1 * rulesScript.applyRules()
	}

	def "runMainScript mixes in validation term operators for closure validator"() {
		setup:
		  rulesScript.applyRules() >> { createValidatorClosureTerm() | createValidatorClosureTerm() }
		when:
		  runMainScript()
		then:
		  notThrown(MissingMethodException)
	}

	def "runMainScript mixes in term operators"() {
		setup:
		  rulesScript.applyRules() >> { createValidationTerm()[''] }
		when:
		  runMainScript()
		then:
		  notThrown(MissingMethodException)
	}

	def "runMainScript mixes in vaidation term operators"() {
		setup:
		  rulesScript.applyRules() >> { createValidationTerm() | createValidationTerm() }
		when:
		  runMainScript()
		then:
		  notThrown(MissingMethodException)
	}

	def "runMainScript mixes in closure operators"() {
		setup:
		  rulesScript.applyRules() >> { { -> } | { -> } }
		when:
		  runMainScript()
		then:
		  notThrown(MissingMethodException)
	}
}
