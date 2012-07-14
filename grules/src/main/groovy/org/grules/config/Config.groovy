package org.grules.config

import java.util.logging.Handler
import java.util.logging.Level

/**
 * A singleton class with grules configuration parameters.
 */
class Config {

	private final Map<String, Object> parameters
	static final String NOT_VALIDATED_PARAMETERS_ACTION_PARAMETER_NAME = 'notValidatedParametersAction'
	static final String DEFAULT_GROUP_PARAMETER_NAME = 'defaultGroup'
	static final String THREAD_POOL_SIZE_PARAMETER_NAME = 'threadPoolSize'
	static final String LOG_LEVEL_PARAMETER_NAME = 'logLevel'
	static final String LOGGER_HANDLER_PARAMETER_NAME = 'loggerHandler'
	static final String GROUPS_PARAMETER_NAME = 'groups'
	static final String TYPE_CHECKED_FUNCTIONS_PARAMETER_NAME = 'typeCheckedFunctions'
	static final String RESOURCE_BUNDLE_PATH_PARAMETER_NAME = 'resourceBundlePath'

	Config(Map<String, Object> properties) {
		this.parameters = properties
	}

	/** Thread pool size for running rules scripts. */
	int getThreadPoolSize() {
		parameters[THREAD_POOL_SIZE_PARAMETER_NAME]
	}

	/** Group to use when no group is specified. */
	String getDefaultGroup() {
		String group = parameters[DEFAULT_GROUP_PARAMETER_NAME]
		if (!(group in groups)) {
			throw new ConfigException("Group $group is not in group list $groups")
		}
		group
	}
	
	/** Set of available groups. */
	Set<String> getGroups() {
		parameters[GROUPS_PARAMETER_NAME] as Set
	}
	
	/** Level of logging used by the rule engine. */
	Level getLogLevel() {
		parameters[LOG_LEVEL_PARAMETER_NAME]
	}
	
	/** Logger handler used by the rule engine. */
	Handler getLoggerHandler() {
		parameters[LOGGER_HANDLER_PARAMETER_NAME]
	}

	/** Action performed when there is no rule for some parameter. */
	OnValidationEventAction getNotValidatedParametersAction() {
		parameters[NOT_VALIDATED_PARAMETERS_ACTION_PARAMETER_NAME]
	}
	
	/** Indicates if functions must be statically type checked. */
	boolean getFunctionsTypeChecked() {
		parameters[TYPE_CHECKED_FUNCTIONS_PARAMETER_NAME]
	}
	
	/** Resource bundle for error messages. */
	String getResourceBundlePath() {
		parameters[RESOURCE_BUNDLE_PATH_PARAMETER_NAME]
	}
	
	/** Returns all configuration parameters as a string. */
	@Override
	String toString() {
		parameters
	}
}