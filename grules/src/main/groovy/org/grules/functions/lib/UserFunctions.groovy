package org.grules.functions.lib;import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress

import org.grules.ValidationException
import org.grules.ast.Functions

@Functions
class UserFunctions {

	boolean isEmail(String value) {
		try {
  		InternetAddress emailAddr = new InternetAddress(value)
			emailAddr.validate()
			true
		} catch (AddressException ex) {
			false
		}
	} 
	
	boolean isStrongPassword(String value, List<String> mediumRegexps, List<String> strongRegexps) {
		if (!mediumRegexps.every {String regexp -> value.matches(regexp)}) {
			throw new ValidationException((PasswordStrength.simpleName): PasswordStrength.WEAK)
		} else if (!strongRegexps.every {String regexp -> value.matches(regexp)}) {
			throw new ValidationException((PasswordStrength.simpleName): PasswordStrength.MEDIUM)
		} else {
			true
		}
	}
}