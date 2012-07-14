package org.grules.script

import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import org.codehaus.groovy.runtime.InvokerInvocationException
import org.grules.GrulesLogger
import org.grules.ValidationErrorProperties
import org.grules.config.Config
import org.grules.config.ConfigException
import org.grules.config.OnValidationEventAction
import org.grules.script.expressions.Operators

import com.google.inject.Inject

/**
 * The RuleEngine class manages execution of rules scripts.
 */
class RuleEngine {

	private final Config config
	private final ExecutorService executorService
	private final RulesScriptFactory rulesScriptFactory

	@Inject
	RuleEngine(Config config, RulesScriptFactory rulesScriptFactory) {
		this.config = config
		this.rulesScriptFactory = rulesScriptFactory
		executorService = Executors.newFixedThreadPool(config.threadPoolSize)
	}

	/**
	 * Creates a closure that applies a rules script to given parameters values. Parameters is a map where keys are 
	 * parameters names and values are parameters values (<code>Map&lt;String, Object></code>)
	 */
	Closure<RulesScriptResult> newExecutor(Class<? extends Script> rulesScriptClass) {
    String defaultGroup = config.defaultGroup
		return { Map<String, Object> parameters ->
			RulesScript script = runMainScript(rulesScriptClass, [(defaultGroup): parameters])
			RulesScriptResult scriptResult = RulesScriptResultFetcher.fetchResult(script, defaultGroup, parameters)
			checkMissingFlatParameters(scriptResult.notValidatedParameters)
			scriptResult
		}
	}


	/**
	 * Creates a closure that applies a rules script to given parameters values. Parameters is a map where keys are 
	 * parameters groups and values are maps of the first type (<code>Map&lt;ParametersGroup, Map&lt;String, Object>>
	 * </code>)
	 */
	Closure<RulesScriptGroupResult> newGroupExecutor(Class<? extends Script> rulesScriptClass) {
		return {Map<String, Map<String, Object>> parameters ->
			parameters.each {String group, groupParameters ->
				if (!config.groups.contains(group)) {
					throw new InvalidGroupException(group)
				}
			}
			RulesScript script = runMainScript(rulesScriptClass, parameters)
			RulesScriptGroupResult scriptResult = RulesScriptResultFetcher.fetchGroupResult(script, parameters)
			checkMissingGroupParameters(scriptResult.notValidatedParameters)
			scriptResult
		}
	}

	/**
	 * Runs a main rules script that includes all others.
	 */
	private RulesScript runMainScript(Class<? extends Script> scriptClass, 
		  Map<String, Map<String,Object>> parameters) {
		RulesScript script = rulesScriptFactory.newInstanceMain(scriptClass, parameters)
    try {
			executorService.submit{runScript(script)}.get()
		} catch (ExecutionException e) {
			throw e.cause
		} catch (InvokerInvocationException e) {
			throw e.cause
		}		
		script
	}

	/**
	 * Runs an included rules script.
	 */
	protected void runIncludedScript(Class<? extends Script> scriptClass,
			List<Class<? extends Script>> scriptsChain, Binding binding, Map<String, Set<String>> missingRequiredParameters,	
			Map<String, Map<String, ValidationErrorProperties>> invalidParameters,
			Map<String, Set<String>> parametersWithMissingDependency) {
		RulesScript rulesScript = rulesScriptFactory.newInstanceIncluded(scriptClass, scriptsChain,
			binding, missingRequiredParameters, invalidParameters, parametersWithMissingDependency)
		runScript(rulesScript)
	}
	
	/**
	 * Runs a rules script.
	 */
	private void runScript(RulesScript script) {
		use(Operators.declaredClasses as List, script.&applyRules)
	}

	/**
	 * Checks missing parameters for a rules script with several sections.
	 */
	private void checkMissingGroupParameters(Map<String, Map<String, String>> notValidatedParameters) {
		if (!notValidatedParameters.isEmpty()) {
			checkMissingParameters(new NotValidatedParametersGroupException(notValidatedParameters))
		}
	}
	
	/**
	 * Checks missing parameters for a rules script with one default section.
	 */
	private void checkMissingFlatParameters(Map<String, String> notValidatedParameters) {
		if (!notValidatedParameters.isEmpty()) {
			checkMissingParameters(new NotValidatedParametersFlatException(notValidatedParameters))
		}
	}
	
	private void checkMissingParameters(NotValidatedParametersException notValidatedParametersException) {
		if (config.notValidatedParametersAction != OnValidationEventAction.IGNORE) {
			switch (config.notValidatedParametersAction) {
				case OnValidationEventAction.ERROR: 
				    throw notValidatedParametersException 
				case OnValidationEventAction.LOG: 
				    GrulesLogger.warn("Missing parameters: $notValidatedParametersException.parameters") 
						break
				default: 
				    throw new ConfigException("Unknown action value ($config.notValidatedParametersAction)")
			}
		}
	}
}