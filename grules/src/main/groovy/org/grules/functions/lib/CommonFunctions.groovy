package org.grules.functions.lib

import org.grules.ValidationException
import org.grules.ast.Converter
import org.grules.ast.Functions

/**
 * Standard grules converters and validators. 
 */
@Functions
class CommonFunctions {
	
	Integer abs(Integer value) {
		Integer result = Math.abs(value)
		if (result < 0) {
			throw new NumberFormatException()
		}
		result
	}
	
	Long abs(Long value) {
		Long result = Math.abs(value)
		if (result < 0) {
			throw new NumberFormatException()
		}
		result
	}
	
	Float abs(Float value) {
		Float result = Math.abs(value)
		if (result < 0) {
			throw new NumberFormatException()
		}
		result
	}
	
	Double abs(Double value) {
		Double result = Math.abs(value)
		if (result < 0) {
			throw new NumberFormatException()
		}
		result
	}

  Number add(Number value, Number number) {
		value + number
	}

	Number div(Number value, Number number) {
		if (number == 0) {
			throw new ValidationException('Division by zero')
		}
		value / number
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

  boolean isFalse(value) {
    !value
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

	Number mod(Long value, Long number) {
		value % number
	}
	
	Number mult(Number value, Number number) {
		value * number
	}
	
	def nop(value) {
		value
	}
	
	Double pow(Double value, Double number) {
		Math.pow(value, number)
	}
	
	Double round(Double value) {
		Math.round(value)
	}
  
  boolean isTrue(value) {
    value
  }
}