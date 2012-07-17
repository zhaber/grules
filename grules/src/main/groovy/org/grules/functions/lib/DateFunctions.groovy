package org.grules.functions.lib

import org.grules.functions.PrimitiveTypesConverters
import org.joda.time.DateTime
import org.grules.ast.Functions


/**
 * Converters and validators for dates. 
 */
@Functions
class DateFunctions {
	static final Integer MAX_AGE = 120
	
	boolean isAfter(Date value, Date date) {
		value.after(date)
	}

	boolean isAfterNow(Date value) {
		value.after(new Date())
	}
	
	boolean isBefore(Date value, Date date) {
		value.before(date)
	}

	boolean isBeforeNow(Date value) {
		value.before(new Date())
	}
	
	boolean isBirthDateAlive(Date value) {
		DateTime dateTimeValue = new DateTime(value)
		// Make the second operand a constant after the elixir of immortality is discovered
		dateTimeValue.isAfter((new DateTime()).minusYears(MAX_AGE))
	}

	boolean isBirthYearAlive(Integer value) {
		DateTime now = new DateTime()
		DateTime dateTimeValue = now.withYear(value)
		dateTimeValue.isAfter(now.minusYears(MAX_AGE))
	}
	
	Date toDate(String value, String pattern, Locale locale = Locale.default) {
		PrimitiveTypesConverters.toDate(value, pattern, locale)
	}

}
