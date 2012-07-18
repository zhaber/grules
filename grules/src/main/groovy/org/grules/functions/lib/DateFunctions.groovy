
package org.grules.functions.lib

import org.grules.ast.Functions
import org.joda.time.DateTime


/**
 * Converters and validators for dates. 
 */
@Functions
class DateFunctions {
	static final Integer MAX_HUMAN_AGE_YEARS = 120

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
		DateTime minBirthDate = DateTime.now().minusYears(MAX_HUMAN_AGE_YEARS)
		dateTimeValue.isAfter(minBirthDate) && dateTimeValue.isBeforeNow()
	}

	boolean isBirthYearAlive(Integer value) {
		DateTime dateTimeValue = DateTime.now().withYear(value)
		isBirthDateAlive(dateTimeValue.toDate())
	}

}
