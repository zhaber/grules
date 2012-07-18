package org.grules.functions.lib

import org.grules.ValidationException
import org.grules.ast.Converter
import org.grules.ast.Functions

/**
 * Standard grules converters and validators. 
 */
@Functions
class CommonFunctions {
	
	Number abs(Number value) {
		Number result = Math.abs(value)
		if (result < 0) {
			throw new NumberFormatException()
		}
		result
	}

	boolean eq(value1, value2) {
		value1 == value2
	}

	@Converter
	Boolean inverse(Boolean value) {
		!value
	}
	
	boolean isAny(List values, Closure validator) {
		values.any(validator)
	}

	boolean isEmpty(List value) {
		value.isEmpty()
	}
	
	boolean isEmpty(String value) {
		value.isEmpty()
  }

	boolean isEven(Integer value) {
		!isOdd(value)
	}

	boolean isEvery(List values, Closure validator) {
		values.every(validator)
	}

	boolean isIn(value, List objects) {
		value in objects
	}

	boolean isOdd(Integer value) {
		(value & 1) == 1
	}

	boolean isStrongPassword(String value, List<String> mediumRegexps, List<String> strongRegexps) {
		if (!mediumRegexps.every {String regexp -> value.matches(regexp)}) {
			throw new ValidationException((PasswordStrength.simpleName): PasswordStrength.WEAK)
		} else if (!strongRegexps.every {String regexp -> value.matches(regexp)}) {
			throw new ValidationException((PasswordStrength.simpleName): PasswordStrength.MEDIUM)
		} else {
			true
		}
	}

	def nop(value) {
		value
	}
}