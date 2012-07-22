package org.grules.functions.lib

import static org.grules.TestScriptEntities.*

import org.grules.functions.ConverterBooleanResult

import spock.lang.Specification

class CommonFunctionsTest extends Specification {
	
	CommonFunctions commonFunctions = new CommonFunctions()
	
	def "eq"() {
		expect:
			commonFunctions.eq(PARAMETER_VALUE, PARAMETER_VALUE)
	}
	
	def "inverse"() {
		expect:
			(commonFunctions.inverse(false) as ConverterBooleanResult).value
	}
	
	def "isAny"() {
		expect:
			commonFunctions.isAny([PARAMETER_VALUE, INVALID_PARAMETER_VALUE]) {it == PARAMETER_VALUE} 
	}

	def "isEmpty"() {
		expect:
			commonFunctions.isEmpty('')
			commonFunctions.isEmpty([])
	}
	
  def "isFalse"() {
    expect:
      commonFunctions.isFalse('')
  }

	def "isEvery"() {
		expect:
			commonFunctions.isEvery([PARAMETER_VALUE, PARAMETER_VALUE]) {it == PARAMETER_VALUE}
		  !commonFunctions.isEvery([PARAMETER_VALUE, INVALID_PARAMETER_VALUE]) {it == PARAMETER_VALUE}
	}

	def "isIn"() {
		expect:
			commonFunctions.isIn(PARAMETER_VALUE, [PARAMETER_VALUE, INVALID_PARAMETER_VALUE]) 
	}

	def "nop"() {
		expect:
			commonFunctions.nop(PARAMETER_VALUE) == PARAMETER_VALUE
	}
	
  def "isTrue"() {
    expect:
      commonFunctions.isTrue(1)
  }
}