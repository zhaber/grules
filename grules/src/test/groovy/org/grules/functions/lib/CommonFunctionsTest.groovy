package org.grules.functions.lib

import static org.grules.TestScriptEntities.*

import org.grules.ValidationException
import org.grules.functions.ConverterBooleanResult

import spock.lang.Specification

class CommonFunctionsTest extends Specification {
	
	CommonFunctions commonFunctions = new CommonFunctions()
	
	def "abs"() {
		expect:
		  commonFunctions.abs(-1) == 1 
	}
	
	def "add"() {
		expect:
			commonFunctions.add(1, 2) == 3
	}

	def "div"() {
		expect:
			commonFunctions.div(0.2, 2) == 0.1
	}
	
	def "division by zero"() {
		when:
			commonFunctions.div(1, 0)
		then:
		  thrown(ValidationException)
	}
		
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
	
	def "isEven"() {
		expect:
			!commonFunctions.isEven(1)
			commonFunctions.isEven(0)
			!commonFunctions.isEven(-1)
	}
  
  def "isFalse"() {
    expect:
      commonFunctions.isFalse('')
  }
	
	def "isOdd"() {
		expect:
			commonFunctions.isOdd(1)
			!commonFunctions.isOdd(0)
			commonFunctions.isOdd(-1)
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

	def "mod"() {
		expect:
			commonFunctions.mod(5, 2) == 1
	}
	
	def "mult"() {
		expect:
			commonFunctions.mult(0.1, 2) == 0.2
	}

	def "nop"() {
		expect:
			commonFunctions.nop(PARAMETER_VALUE) == PARAMETER_VALUE
	}
	
	def "pow"() {
		expect:
		  commonFunctions.pow(0.5, 2) == 0.25
	}
	
	def "round"() {
		expect:
		  commonFunctions.round(0.8) == 1
	}
  
  def "isTrue"() {
    expect:
      commonFunctions.isTrue(1)
  }
}