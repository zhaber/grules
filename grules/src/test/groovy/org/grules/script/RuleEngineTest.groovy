package org.grules.script

import static org.grules.TestRuleEntriesFactory.*
import static org.grules.TestScriptEntities.*

import org.grules.EmptyRulesScript
import org.grules.Grules
import org.grules.GrulesInjector
import org.grules.config.Config
import org.grules.config.ConfigFactory
import org.grules.config.OnValidationEventAction

import spock.lang.Specification

class TestRulesScript extends Script {
	def run() {
		(this as RulesScript).applyRule(PARAMETER_NAME, PARAMETER_VALUE, newEmptyRuleClosure())
	}
}

class RuleEngineTest extends Specification {
	
	RulesScript rulesScript
	
	def setup() {
		rulesScript = Mock()
	}
	
	RuleEngine createRuleEngineWithValidationErrorAction() {
		def config = ConfigFactory.createDefaultConfig()
		config.parameters.put(Config.NOT_VALIDATED_PARAMETERS_ACTION_PARAMETER_NAME, OnValidationEventAction.ERROR)
		new RuleEngine(config, new RulesScriptFactory())
	}

	def runMainScript() {
		RuleEngine ruleEngine = new RuleEngine(GrulesInjector.config, new RulesScriptFactory() {
			@Override
			RulesScript newInstanceMain(Class<? extends Script> scriptClass, Map<String, Map<String, Object>> parameters, 
		      Map<String, Closure> functions) {
				rulesScript
			}
		})
		ruleEngine.runMainScript(Script, [:], [:])
	}

	def "Create new preprocessor for grouped parameters"() {
		setup:
		  def parameters = [(GROUP): [(PARAMETER_NAME): PARAMETER_VALUE]]
			RulesScriptGroupResult result = Grules.applyGroupRules(TestRulesScript, parameters)
		expect:
			result.cleanParameters == [(GROUP): [(PARAMETER_NAME): PARAMETER_VALUE]]
	}
	
	def "Create new preprocessor for default group"() {
		setup:
			RulesScriptResult result = Grules.applyRules(TestRulesScript, (PARAMETER_NAME): PARAMETER_VALUE)
		expect:
			result.cleanParameters == [(PARAMETER_NAME): PARAMETER_VALUE]
	}

	def "NotValidatedParametersException is thrown when there is parameter without rule (for non-grouped parameters)"() {
		setup:
			def preprocessor = createRuleEngineWithValidationErrorAction().newExecutor(EmptyRulesScript, [:])
		when:
	  	preprocessor((PARAMETER_NAME): PARAMETER_VALUE)
		then:
		  NotValidatedParametersFlatException e = thrown(NotValidatedParametersException)
			e.parameters == [(PARAMETER_NAME): PARAMETER_VALUE]
	}
	
	def "NotValidatedParametersException is thrown when there is parameter without rule (for grouped parameters)"() {
		setup:
			def preprocessor = createRuleEngineWithValidationErrorAction().newGroupExecutor(EmptyRulesScript, [:])
		when:
			preprocessor((GROUP): [(PARAMETER_NAME): PARAMETER_VALUE]) 
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
		  rulesScript.applyRules() >> {newValidatorClosureTerm() | newValidatorClosureTerm()}
		when:
		  runMainScript()
		then:
		  notThrown(MissingMethodException)
	}
	
	def "runMainScript mixes in term operators"() {
		setup:
		  rulesScript.applyRules() >> {newValidationTerm()['']}
		when:
		  runMainScript()
		then:
		  notThrown(MissingMethodException)
	}

	def "runMainScript mixes in vaidation term operators"() {
		setup:
		  rulesScript.applyRules() >> {newValidationTerm() | newValidationTerm()}
		when:
		  runMainScript()
		then:
		  notThrown(MissingMethodException)
	}
	
	def "runMainScript mixes in closure operators"() {
		setup:
		  rulesScript.applyRules() >> {{->} | {->}}
		when:
		  runMainScript()
		then:
		  notThrown(MissingMethodException)
	} 
}