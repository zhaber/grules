package org.grules

import java.util.logging.Level

import org.grules.config.Config
import org.grules.config.ConfigFactory
import org.grules.http.HttpRequestParametersGroup

class TestScriptEntities {
	private static final Config DEFAULT_CONFIG = new ConfigFactory().createDefaultConfig()

	static final FALSE_PARAMETER = []
	static final ADDITIONAL_FUNCTION = 'additionalFunction'
	static final PARAMETER_NAME = 'parameterName'
	static final PARAMETER_NAME_AUX = 'parameterNameAux'
	static final PARAMETER_VALUE = 'parameterValue'
	static final PARAMETER_VALUE_AUX = 'parameterValueAux'
	static final CLEAN_PARAMETER_VALUE = 'cleanParameterValue'
	static final VARIABLE_NAME = 'variableName'
	static final VARIABLE_NAME_AUX = 'variableNameAux'
	static final VARIABLE_VALUE = 'variableValue'
	static final CONSTANT_NAME = 'constantName'
	static final CONSTANT_VALUE = 'constantValue'
	static final DEFAULT_VALUE = 'defaultValue'
	static final DATE_FORMAT = 'yyyy-MM-dd'
	static final ERROR_MSG = 'errorMessage'
	static final ERROR_MSG_AUX = 'errorMessageAux'
	static final VALID_INTEGER = 1
	static final VALID_INTEGER_STRING = VALID_INTEGER.toString()
	static final FUNCTION_ARGUMENT = VALID_INTEGER_STRING
	static final FUNCTION_NAME = 'functionName'
	static final VALID_PARAMETER = VALID_INTEGER_STRING
	static final INVALID_PARAMETER_VALUE = '1a'
	static final SUBRULES_SEQ_NAME = 'subrulesSeqName'
	static final GROUP = DEFAULT_CONFIG.defaultGroup
	static final GROUP_AUX = HttpRequestParametersGroup.GET.name()
	static final CONFIG_LOG_LEVEL = Level.INFO
  static final NOT_VALIDATED_PARAMETERS_ACTION = DEFAULT_CONFIG.notValidatedParametersAction
}
