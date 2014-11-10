package org.grules.functions.lib

import org.joda.time.DateTime

import spock.lang.Specification

class DateFunctionsTest extends Specification {

	private final DateFunctions dateFunctions = new DateFunctions()
	private final Date nowDate = new Date()
	private final DateTime nowDateTime = new DateTime(nowDate)
	private static final YEARS_DIFFERENCE = 2
	private final DateTime beforeDateTime = nowDateTime.minusYears(YEARS_DIFFERENCE)
	private final DateTime afterDateTime = nowDateTime.plusYears(YEARS_DIFFERENCE)
	private final Date beforeDate = beforeDateTime.toDate()
	private final Date afterDate = afterDateTime.toDate()
	private final DateTime invalidBirthDateTime = nowDateTime.minusYears(DateFunctions.MAX_HUMAN_AGE_YEARS
    + YEARS_DIFFERENCE)

	def "isAfter"() {
		expect:
		  dateFunctions.isAfter(afterDate, beforeDate)
	}

	def "isAfterNow"() {
		expect:
		  dateFunctions.isAfterNow(afterDate)
			!dateFunctions.isAfterNow(beforeDate)
	}

	def "isBefore"() {
		expect:
		  dateFunctions.isBefore(beforeDate, afterDate)
	}

  def "isBeforeNow"() {
		expect:
		  dateFunctions.isBeforeNow(beforeDate)
			!dateFunctions.isBeforeNow(afterDate)
	}

	def "isBirthDateAlive"() {
		expect:
		  dateFunctions.isBirthDateAlive(beforeDate)
		  !dateFunctions.isBirthDateAlive(afterDate)
		  !dateFunctions.isBirthDateAlive(invalidBirthDateTime.toDate())
	}

	def "isBirthYearAlive"() {
		expect:
		  dateFunctions.isBirthYearAlive(beforeDateTime.year)
		  !dateFunctions.isBirthYearAlive(afterDateTime.year)
		  !dateFunctions.isBirthYearAlive(invalidBirthDateTime.year)
	}

}
