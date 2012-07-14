package org.grules

import org.grules.config.Config
import org.grules.config.ConfigFactory
import org.grules.config.OnValidationEventAction

import org.grules.http.HttpRequestParametersGroup

class TestScriptEntities {
	private static final Config DEFAULT_CONFIG = new ConfigFactory().createDefaultConfig()
	
	static final String PARAMETER_NAME = 'parameterName'
	static final String PARAMETER_NAME_AUX = 'parameterNameAux'
	static final String PARAMETER_VALUE = 'parameterValue'
	static final String PARAMETER_VALUE_AUX = 'parameterValueAux'
	static final String CLEAN_PARAMETER_VALUE = 'cleanParameterValue'
	static final String VARIABLE_NAME = 'variableName'
	static final String VARIABLE_NAME_AUX = 'variableNameAux'
	static final String VARIABLE_VALUE = 'variableValue'
	static final String CONSTANT_NAME = 'constantName'
	static final String CONSTANT_VALUE = 'constantValue'
	static final String DEFAULT_VALUE = 'defaultValue'
	static final String DATE_FORMAT = 'yyyy-MM-dd'
	static final String ERROR_MSG = 'errorMessage'
	static final String ERROR_MSG_AUX = 'errorMessageAux'
	static final String VALID_INTEGER_STRING = '1'
	static final int VALID_INTEGER = 1
	static final String FUNCTION_ARGUMENT = VALID_INTEGER_STRING
	static final String FUNCTION_NAME = 'functionName'
	static final String VALID_PARAMETER = VALID_INTEGER_STRING
	static final String INVALID_PARAMETER = '1a'
	static final String SUBRULES_SEQ_NAME = 'subrulesSeqName'
	static final String GROUP = DEFAULT_CONFIG.defaultGroup
	static final String GROUP_AUX = HttpRequestParametersGroup.GET.name()
	static final int OVERRIDDEN_THREAD_POOL_SIZE = DEFAULT_CONFIG.threadPoolSize + 1
  static final OnValidationEventAction NOT_VALIDATED_PARAMETERS_ACTION = DEFAULT_CONFIG.notValidatedParametersAction
}
