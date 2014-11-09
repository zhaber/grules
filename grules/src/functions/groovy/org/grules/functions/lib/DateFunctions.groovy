package org.grules.functions.lib

import org.joda.time.DateTime
import org.grules.functions.Functions

/**
 * Converters and validators for dates.
 */
@Functions
class DateFunctions {
  static final Integer MAX_HUMAN_AGE_YEARS = 120

  /**
   * Check that a date is after the specified one.
   */
  boolean isAfter(Date value, Date date) {
    value.after(date)
  }

  /**
   * Check that a date is after now.
   */
  boolean isAfterNow(Date value) {
    value.after(new Date())
  }

  /**
   * Check that a date is before the specified one
   */
  boolean isBefore(Date value, Date date) {
    value.before(date)
  }

  /**
   * Check that a date is before now.
   */
  boolean isBeforeNow(Date value) {
    value.before(new Date())
  }

  /**
   * Check that a date is before the current year (or the current one) but not more than 120 years in the past.
   */
  boolean isBirthDateAlive(Date value) {
    DateTime dateTimeValue = new DateTime(value)
    DateTime minBirthDate = DateTime.now().minusYears(MAX_HUMAN_AGE_YEARS)
    dateTimeValue.isAfter(minBirthDate) && dateTimeValue.isBeforeNow()
  }

  /**
   * Check that a year is before the current year (or the current one) but not more than 120 years in the past.
   */
  boolean isBirthYearAlive(Integer value) {
    DateTime dateTimeValue = DateTime.now().withYear(value)
    isBirthDateAlive(dateTimeValue.toDate())
  }

}
