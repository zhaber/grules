package org.grules.functions.lib

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

import org.grules.ValidationException
import org.grules.ast.Converter
import org.grules.ast.Functions

/**
 * Standard grules converters and validators. 
 */
@Functions
class TypeFunctions {
	
	boolean isBigDecimal(String value) {
		//Groovy 2.0: StringGroovyMethods.isBigDecimal(value)
		value.isBigDecimal()
	}
	
	boolean isInt(String value) {
		// Groovy 2.0: StringGroovyMethods.isInteger()
		value.isInteger()
	}

	boolean isLong(String value) {
		// Groovy 2.0: StringGroovyMethods.isLong(value) 
		value.isLong()
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

	BigDecimal toBigDecimal(String value) {
	  if (value.isBigDecimal()) {
		  value.toBigDecimal()
	  } else {
		  throw new ValidationException()
	  }
	}
	
	@Converter
	Boolean toBoolean(value) {
		value ? true : false
	}

	BigDecimal toNaturalBigDecimal(String value) {
		BigDecimal bigDecimalValue = toBigDecimal(value)
		if (bigDecimalValue >= 0) {
			bigDecimalValue
		} else {
			throw new ValidationException()
		}
	}

	BigDecimal toPositiveBigDecimal(String value) {
		BigDecimal bigDecimalValue = toBigDecimal(value)
		if (bigDecimalValue > 0) {
			bigDecimalValue
		} else {
			throw new ValidationException()
		}
	}

	Long toLong(String value) {
		if (value.isLong()) {
			value.toLong()
		} else {
			throw new ValidationException()
		}
	}

	Long toNaturalLong(String value) {
		Long longValue = toLong(value)
		if (longValue >= 0) {
			longValue
		} else {
			throw new ValidationException()
		}
	}

	Long toPositiveLong(String value) {
		Long longValue = toLong(value)
		if (longValue > 0) {
			longValue
		} else {
			throw new ValidationException()
		}
	}

	Integer toInt(String value) {
	  if (value.isInteger()) {
		  value.toInteger()
	  } else {
	    throw new ValidationException()
	  }
	}

	Integer toNaturalInt(String value) {
		Integer intValue = toInt(value)
		if (intValue >= 0) {
			intValue
		} else {
			throw new ValidationException()
		}
	}

	Integer toPositiveInt(String value) {
		Integer intValue = toInt(value) 
		if (intValue > 0) {
			intValue
		} else {
			throw new ValidationException()
		}
	}
	
	Date toDate(String value, String pattern, Locale locale = Locale.default) {
		DateFormat dateFormatter = new SimpleDateFormat(pattern, locale)
		try {
			dateFormatter.parse(value)
		} catch (ParseException e) {
			throw new ValidationException(e.message)
		}
	}
}