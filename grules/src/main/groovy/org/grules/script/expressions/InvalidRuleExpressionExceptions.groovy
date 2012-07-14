package org.grules.script.expressions

import groovy.transform.InheritConstructors

import org.codehaus.groovy.syntax.Types
import org.grules.GrulesException

/**
 * Signals an invalid subrules sequence.
 */
class InvalidSubrulesSeqException extends GrulesException {
	
	InvalidSubrulesSeqException(SubrulesSeq subrulesSeq, Subrule subrule) {
		super("Subrules sequence '$subrulesSeq >> $subrule' is invalid")
	}
	
	InvalidSubrulesSeqException(subrulesSeq) {
		super("Subrules sequence '$subrulesSeq' of type ${subrulesSeq?.class} is invalid")
	}
}

/**
 * Signals an invalid subrule expression. 
 */
class InvalidSubruleException extends GrulesException {
	
	InvalidSubruleException(subrule) {
		super("Subrule '$subrule' is invalid")
	}
}

/**
 * Signals an invalid term expression.
 */
@InheritConstructors
class InvalidTermException extends GrulesException {
	
	InvalidTermException(term) {
		super("Term '$term' is invalid")
	}
}

/**
 * Signals an invalid validator.
 */
class InvalidValidatorException extends GrulesException {
  final returnValue
  final String methodName
 
  InvalidValidatorException(returnValue, String methodName) {
	  this.returnValue = returnValue
	  this.methodName = methodName
  }
  
  InvalidValidatorException(returnValue, String methodName, String parameterName) {
	  super("Validator $methodName in rule for $parameterName returned $returnValue of type ${returnType.class}. " +
			 		 'Expected type is boolean or Boolean.')
  }
}

/**
 * Thrown to indicate that the term returns not a boolean type.
 */
class InvalidBooleanTermException extends GrulesException {
	
	InvalidBooleanTermException(term) {
		super("Expression '$term' has type ${term.class}. Expected type is Boolean")
	}
	
	InvalidBooleanTermException(Term leftTerm, Term rightTerm, int operator) {
		super("Rule expression '$leftTerm " + Types.getText(operator) + " $rightTerm' is invalid, " + 
          "$leftTerm is of type ${leftTerm.class}, $rightTerm is of type ${rightTerm.class}.")
	}
	
	InvalidBooleanTermException(Term term, int operator) {
		super("Rule expression '" + Types.getText(operator) + term + "' is invalid. Term is of type ${term.class}.")
	}

	InvalidBooleanTermException(term, String name) {
		super("Method $name return value <$term> of type ${term.class}. Expected type is Boolean")
	}

}