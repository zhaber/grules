package org.grules.script.expressions

import org.codehaus.groovy.syntax.Types
import org.grules.GrulesException

/**
 * Thrown to indicate that the term returns not a boolean type.
 */
class InvalidBooleanTermException extends GrulesException {
	
	InvalidBooleanTermException(term) {
		super("Expression '$term' has type ${term.class}. Expected type is Boolean")
	}
	
	InvalidBooleanTermException(Term leftTerm, Term rightTerm, Integer operator) {
		super("Rule expression '$leftTerm " + Types.getText(operator) + " $rightTerm' is invalid, " + 
          "$leftTerm is of type ${leftTerm.class}, $rightTerm is of type ${rightTerm.class}.")
	}
	
	InvalidBooleanTermException(Term term, Integer operator) {
		super("Rule expression '" + Types.getText(operator) + term + "' is invalid. Term is of type ${term.class}.")
	}

	InvalidBooleanTermException(term, String name) {
		super("Method $name return value <$term> of type ${term.class}. Expected type is Boolean")
	}

}