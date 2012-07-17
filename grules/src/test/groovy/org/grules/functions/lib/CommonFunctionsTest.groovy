package org.grules.functions.lib

import org.grules.ValidationException
import org.grules.functions.ConverterBooleanResult

import spock.lang.Specification

class CommonFunctionsTest extends Specification {
	
	CommonFunctions commonFuns = new CommonFunctions()

	def "isStrong password returns true for strong password"() {
		expect:
			commonFuns.isStrongPassword('a0+', [/.*[a-z].*/, /.*[0-9].*/], [/.*\+.*/])
	}
	
	def "isStrong password throws excpetion for invalid password"() {
		when:
		  commonFuns.isStrongPassword('a0', [/.*[a-z].*/, /.*[0-9].*/], [/.*\+.*/])
		then:
		  ValidationException e = thrown(ValidationException)
		expect:
		  e.errorProperties.hasErrorProperty(PasswordStrength.simpleName)
	}
	
	def "Even/odd check"() {
		expect:
		  commonFuns.isOdd(value) == oddness
			commonFuns.isEven(value) == !oddness
		where:
		  value | oddness 
			  1   | true   
			  0   | false  
			 -1   | true
	}
	
	def "to boolean converter"() {
		expect:
		  (commonFuns.toBoolean(value) as ConverterBooleanResult).value == booleanValue
		where:
		  value | booleanValue
		   ''   | false
		   '0'  | true
	      0   | false
	}
	
	def "inverse converter"() {
		expect:
			(commonFuns.inverse(false) as ConverterBooleanResult).value
	}
}