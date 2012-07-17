package org.grules.script.expressions

import org.grules.GrulesException

/**
 * Signals an invalid validator.
 */
class InvalidValidatorException extends GrulesException {
	final returnValue
	final String methodName
 
	InvalidValidatorException(returnValue, String methodName) {
		this.returnValue = returnValue
		this.methodName = methodName
	}
	
	InvalidValidatorException(returnValue, String methodName, String parameterName) {
		super("Validator $methodName in rule for $parameterName returned $returnValue of type ${returnType.class}. " +
						'Expected type is boolean or Boolean.')
	}
}