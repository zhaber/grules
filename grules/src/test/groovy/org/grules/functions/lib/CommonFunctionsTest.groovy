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

	def "isStrong password returns true for strong password"() {
		expect:
			commonFunctions.isStrongPassword('a0+', [/.*[a-z].*/, /.*[0-9].*/], [/.*\+.*/])
	}
	
	def "isStrong password throws excpetion for invalid password"() {
		when:
			commonFunctions.isStrongPassword('a0', [/.*[a-z].*/, /.*[0-9].*/], [/.*\+.*/])
		then:
			ValidationException e = thrown(ValidationException)
		expect:
			e.errorProperties.hasErrorProperty(PasswordStrength.simpleName)
	}

	def "nop"() {
		expect:
			commonFunctions.nop(PARAMETER_VALUE) == PARAMETER_VALUE
	}

}