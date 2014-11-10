package org.grules.functions.lib

import org.grules.ValidationException

import spock.lang.Specification

class UserFunctionsTest extends Specification {

	private final UserFunctions userFunctions = new UserFunctions()

	def "isStrong password returns true for strong password"() {
		expect:
			userFunctions.isStrongPassword('a0+', [/.*[a-z].*/, /.*[0-9].*/], [/.*\+.*/])
	}

	def "isStrong password throws excpetion for invalid password"() {
		when:
			userFunctions.isStrongPassword('a0', [/.*[a-z].*/, /.*[0-9].*/], [/.*\+.*/])
		then:
			ValidationException e = thrown(ValidationException)
		expect:
			e.errorProperties.hasErrorProperty(PasswordStrength.simpleName)
	}

	def "isEmail"() {
		expect:
		  userFunctions.isEmail('user@[1.1.1.1]')
			userFunctions.isEmail('"user\\@"@example.com')
			userFunctions.isEmail('user+label@gmail.com')
		  !userFunctions.isEmail('invalidEmail@')
	}

}
