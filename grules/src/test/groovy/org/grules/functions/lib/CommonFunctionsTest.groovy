package org.grules.functions.lib

import org.grules.ValidationException

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
		  commonFuns.isOdd(a) == oddness
			commonFuns.isEven(a) == !oddness
		where:
		  a  | oddness 
			1  | true   
			0  | false  
			-1 | true
	}
}