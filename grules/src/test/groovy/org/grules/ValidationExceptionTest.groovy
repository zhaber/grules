package org.grules

import static org.grules.TestScriptEntities.*
import spock.lang.Specification

class ValidationExceptionTest extends Specification {

	def "ValidationException with nulls"() {
    when:
      def validationException = new ValidationException([(ValidationErrorProperties.ERROR_ID): null])
		then:
		  !validationException.hasProperty(ValidationErrorProperties.ERROR_ID)
	}
}