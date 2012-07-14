package org.grules.functions.lib

import org.grules.ValidationException
import org.grules.functions.PrimitiveTypesConverters
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

	def nop(value) {
		value
	}

	boolean isAny(List values, Closure validator) {
		values.any(validator)
	}

	boolean isBigDecimal(String value) {
		//Groovy 2.0: StringGroovyMethods.isBigDecimal(value)
		value.isBigDecimal()
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

	boolean isInt(String value) {
		// Groovy 2.0: value.isInteger()
		value.isInteger()
	}

	boolean isLong(String value) {
		// Groovy 2.0: StringGroovyMethods.isLong(value) 
		value.isLong()
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

	boolean isPositiveInt(Integer value) {
		value > 0
	}

	boolean isPositiveBigDecimal(BigDecimal value) {
		value > 0
	}

	boolean isPositiveLong(Long value) {
		value > 0
	}

	Integer mult(Integer value, Integer multiplier) {
		value * multiplier
	}

	BigDecimal toBigDecimal(String value) {
		PrimitiveTypesConverters.toBigDecimal(value)
	}

	BigDecimal toNaturalBigDecimal(String value) {
		PrimitiveTypesConverters.toNaturalBigDecimal(value)
	}

	BigDecimal toPositiveBigDecimal(String value) {
		PrimitiveTypesConverters.toPositiveBigDecimal(value)
	}

	Long toLong(String value) {
		PrimitiveTypesConverters.toLong(value)
	}

	Long toNaturalLong(String value) {
		PrimitiveTypesConverters.toNaturalLong(value)
	}

	Long toPositiveLong(String value) {
		PrimitiveTypesConverters.toPositiveLong(value)
	}

	Integer toInt(String value) {
		PrimitiveTypesConverters.toInt(value)
	}

	Integer toNaturalInt(String value) {
		PrimitiveTypesConverters.toNaturalInt(value)
	}

	Integer toPositiveInt(String value) {
		PrimitiveTypesConverters.toPositiveInt(value)
	}
}