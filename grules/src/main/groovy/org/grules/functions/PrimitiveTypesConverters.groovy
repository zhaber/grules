package org.grules.functions

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

import org.grules.ValidationException
import org.joda.time.DateTime


/**
 * Converters to primitive types. 
 */
class PrimitiveTypesConverters {
	
  static BigDecimal toBigDecimal(String value) {
	  if (value.isBigDecimal()) {
		  value.toBigDecimal()
	  } else {
		  throw new ValidationException()
	  }
  }
	
	static Date toDate(String value, String pattern, Locale locale) {
		DateFormat dateFormatter = new SimpleDateFormat(pattern, locale)
		try {
		  DateTime dateTime = new DateTime(dateFormatter.parse(value))
		  dateTime.toDate()
		} catch (ParseException e) {
		  throw new ValidationException(e.message)
		}
	}
	
	static Integer toInt(String value) {
	  if (value.isInteger()) {
		  value.toInteger()
	  } else {
	    throw new ValidationException()
	  }
	}
	
	static Long toLong(String value) {
		if (value.isLong()) {
			value.toLong()
		} else {
			throw new ValidationException()
		}
	}
	
	static BigDecimal toPositiveBigDecimal(String value) {
		Integer bigDecimalValue = toBigDecimal(value)
		if (value > 0) {
			bigDecimalValue
		} else {
			throw new ValidationException()
		}
	}

	static Integer toPositiveInt(String value) {
		Integer intValue = toInt(value) 
		if (value > 0) {
			intValue
		} else {
			throw new ValidationException()
		}
	}
	
	static Long toPositiveLong(String value) {
		Integer longValue = toLong(value)
		if (value > 0) {
			longValue
		} else {
			throw new ValidationException()
		}
	}

	static BigDecimal toNaturalBigDecimal(String value) {
		Integer bigDecimalValue = toBigDecimal(value)
		if (value >= 0) {
			bigDecimalValue
		} else {
			throw new ValidationException()
		}
	}
		
	static Long toNaturalLong(String value) {
		Integer longValue = toLong(value)
		if (value >= 0) {
			longValue
		} else {
			throw new ValidationException()
		}
	}
	
	static Integer toNaturalInt(String value) {
		Integer intValue = toInt(value)
		if (value >= 0) {
			intValue
		} else {
			throw new ValidationException()
		}
	}
	
}