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
	
  boolean isPositive(Number value) {
    value > 0
  }
  
  boolean isNonnegative(Number value) {
    value >= 0
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
  
  Date toDate(String value, String pattern, Locale locale = Locale.default) {
    DateFormat dateFormatter = new SimpleDateFormat(pattern, locale)
    try {
      dateFormatter.parse(value)
    } catch (ParseException e) {
      throw new ValidationException(e.message)
    }
  }
  
	Double toDouble(String value) {
		if (value.isDouble()) {
			value.toDouble()
		} else {
			throw new ValidationException()
		}
	}

	Enum toEnum(String value, Class enumClass) {
		try {
  		value.asType(enumClass)
		} catch (IllegalArgumentException e) {
		  throw new ValidationException()
		}
	}
	
	Float toFloat(String value) {
		if (value.isFloat()) {
			value.toFloat()
		} else {
			throw new ValidationException()
		}
	}
	
  BigDecimal toNonnegativeBigDecimal(String value) {
		BigDecimal bigDecimalValue = toBigDecimal(value)
		if (bigDecimalValue >= 0) {
			bigDecimalValue
		} else {
			throw new ValidationException()
		}
	}
	
	Double toNonnegativeDouble(String value) {
		Double doubleValue = toDouble(value)
		if (doubleValue >= 0) {
			doubleValue
		} else {
			throw new ValidationException()
		}
	}

	Float toNonnegativeFloat(String value) {
		Float floatValue = toFloat(value)
		if (floatValue >= 0) {
			floatValue
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
	
	Double toPositiveDouble(String value) {
		Double doubleValue = toDouble(value)
		if (doubleValue > 0) {
			doubleValue
		} else {
			throw new ValidationException()
		}
	}

	Float toPositiveFloat(String value) {
		Float floatValue = toFloat(value)
		if (floatValue > 0) {
			floatValue
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
}