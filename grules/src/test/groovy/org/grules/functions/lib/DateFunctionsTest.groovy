package org.grules.functions.lib

import org.joda.time.DateTime

import spock.lang.Specification

class DateFunctionsTest extends Specification {
	
	DateFunctions dateFunctions = new DateFunctions()
	Date nowDate = new Date()
	DateTime nowDateTime = new DateTime(nowDate)
	static final YEARS_DIFFERENCE = 2
	DateTime beforeDateTime = nowDateTime.minusYears(YEARS_DIFFERENCE)
	DateTime afterDateTime = nowDateTime.plusYears(YEARS_DIFFERENCE)
	Date beforeDate = beforeDateTime.toDate()
	Date afterDate = afterDateTime.toDate()
	DateTime invalidBirthDateTime = nowDateTime.minusYears(DateFunctions.MAX_HUMAN_AGE_YEARS + YEARS_DIFFERENCE)
	
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