package org.grules.functions.lib;import javax.mail.internet.AddressException

import javax.mail.internet.InternetAddress

import org.grules.ValidationException
import org.grules.functions.Functions

/**
 * Custom user functions.
 */
@Functions
class UserFunctions {

  /**
   * Validate that this address conforms to the syntax rules of RFC 822.
   */
  boolean isEmail(String value) {
    try {
      InternetAddress emailAddr = new InternetAddress(value)
      emailAddr.validate()
      true
    } catch (AddressException ex) {
      false
    }
  }

  /**
   * Checks that the value is compliant with a strong password policy. A password is:<br>
   * - weak if it does not match some of mediumPasswordRegexes <br>
   * - medium if it does not match some of strongPasswordRegexes<br>
   * - strong if it matches all the given regexes<br>
   * Password is invalid if it is not strong, in this case ValidationException’s parameters field contains password <br>
   * strength (WEAK or MEDIUM), which then can be read on the client side
   */
  boolean isStrongPassword(String value, List<String> mediumRegexps, List<String> strongRegexps) {
    if (!mediumRegexps.every { String regexp -> value.matches(regexp) }) {
      throw new ValidationException((PasswordStrength.simpleName):PasswordStrength.WEAK)
    } else if (!strongRegexps.every { String regexp -> value.matches(regexp) }) {
      throw new ValidationException((PasswordStrength.simpleName):PasswordStrength.MEDIUM)
    } else {
      true
    }
  }
}
