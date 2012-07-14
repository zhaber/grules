package org.grules.script

import groovy.transform.TupleConstructor

import org.grules.ValidationErrorProperties
import org.grules.utils.MapUtils
import org.grules.utils.SetUtils

/**
 * Encapsulates a result of application of preprocessing rules:
 * - required but missing parameters
 * - invalid parameters
 * - parameters that did not pass validation
 * - parameters that depend on a value of another missing parameter
 */
class RulesScriptResultFetcher {

	/**
	 * All parameters names in DSL should be in lowercase, so after input was processed we capitalize parameters names 
	 * back.
	 * @param parameters
	 * @param originalParameters
	 * @return
	 */
	private static Map<String, Map<String, ?>> capitalizeParametersMap(Map<String, Map<String, ?>> parameters,
		  Map<String, Map<String, Object>> originalParameters) {
		parameters.collectEntries { String group, Map<String, ?> groupParameters ->
			if (originalParameters.containsKey(group)) {
				[(group): groupParameters.collectEntries { String parameterName, parameterValue ->
					if (originalParameters[group].containsKey(parameterName.capitalize())) {
						[(parameterName.capitalize()): parameterValue]
					} else {
						[(parameterName): parameterValue]
					}
				}]
			} else {
				[(group): groupParameters]
			}
		}
	}
			
	private static Map<String, Set<String>> capitalizeParametersSet(Map<String, Set<String>> parameters,
		  Map<String, Map<String, Object>> originalParameters) {
		parameters.collectEntries { String group, Set<String> groupParametersName ->
			if (originalParameters.containsKey(group)) {
				[(group): (groupParametersName.collect { String parameterName ->
					if (originalParameters[group].containsKey(parameterName.capitalize())) {
						parameterName.capitalize()
					} else {
						parameterName
					}
				}) as Set]
			} else {
				[(group): groupParametersName]
			}
		}
	}
	
	/**
	 * Fetches result of rules application for a script with several sections. 
	 */
	static RulesScriptGroupResult fetchGroupResult(RulesScript rulesScript, 
		  Map<String, Map<String, Object>> originalParameters) {
		Map<String, Map<String, Object>> cleanParameters = rulesScript.fetchCleanParameters()
		Map<String, Map<String, Object>> notValidatedParameters = rulesScript.fetchNotValidatedParameters()
		new RulesScriptGroupResult(
			  capitalizeParametersMap(cleanParameters, originalParameters), 
			  capitalizeParametersMap(notValidatedParameters, originalParameters),
				capitalizeParametersSet(rulesScript.missingRequiredParameters, originalParameters), 
				capitalizeParametersMap(rulesScript.invalidParameters, originalParameters),
				capitalizeParametersSet(rulesScript.parametersWithMissingDependency, originalParameters))
	}

	/**
	 * Fetches result of rules application for a script with one default section.
	 */
	static RulesScriptResult fetchResult(RulesScript rulesScript, String group, Map<String, Object> parameters) {
		RulesScriptGroupResult result = fetchGroupResult(rulesScript, [(group): parameters])
		Map<String, Object> cleanParameters = MapUtils.nullToEmpty(result.cleanParameters[group])
		Map<String, String> notValidatedParameters = MapUtils.nullToEmpty(result.notValidatedParameters[group])
		Set<String> missingRequiredParameters = SetUtils.nullToEmpty(result.missingRequiredParameters[group])
		Map<String, ValidationErrorProperties> invalidParameters = MapUtils.nullToEmpty(result.invalidParameters[group])
		Set<String> parametersWithMissingDependency = SetUtils.nullToEmpty(result.parametersWithMissingDependency[group])
		new RulesScriptResult(cleanParameters, notValidatedParameters, missingRequiredParameters, invalidParameters,
				parametersWithMissingDependency)
	}
}

/**
 * A tuple class for result of rules application.
 */
interface RulesScriptResultAPI {
	/** Parameters that passed validation */
	abstract getCleanParameters()
	/** Parameters for which a preprocessing rule was not found */
	abstract getNotValidatedParameters()
	/** Parameters that are missing from input but required by a rules script. */
	abstract getMissingRequiredParameters()
	/** Parameters that did no pass validation */
	abstract getInvalidParameters()
	/** Parameters that depend on other parameters that are invalid or missing from input. */
	abstract getParametersWithMissingDependency()
}

@TupleConstructor
class RulesScriptGroupResult implements RulesScriptResultAPI {
	/** {@inheritDoc} */
	final Map<String, Map<String, Object>> cleanParameters
	/** {@inheritDoc} */
	final Map<String, Map<String, String>> notValidatedParameters
	/** {@inheritDoc} */
	final Map<String, Set<String>> missingRequiredParameters
	/** Parameters that did no pass validation GroupName -> ParameterName -> ErrorProperties. */
	final Map<String, Map<String, ValidationErrorProperties>> invalidParameters
	/** {@inheritDoc} */
	final Map<String, Set<String>> parametersWithMissingDependency
}

@TupleConstructor
class RulesScriptResult implements RulesScriptResultAPI {
	/** {@inheritDoc} */
	final Map<String, Object> cleanParameters
	/** {@inheritDoc} */
	final Map<String, String> notValidatedParameters
	/** {@inheritDoc} */
	final Set<String> missingRequiredParameters
	/** Parameters that did no pass validation ParameterName -> ErrorProperties. */
	final Map<String, ValidationErrorProperties> invalidParameters
	/** {@inheritDoc} */
	final Set<String> parametersWithMissingDependency
}