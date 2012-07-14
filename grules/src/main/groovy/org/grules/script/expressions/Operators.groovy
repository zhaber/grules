package org.grules.script.expressions

import org.codehaus.groovy.syntax.Types
import org.grules.ValidationErrorProperties

/**
 * Operators used in rule expressions: and, or, pass a value (<code>>></code>), bind an error (<code>[]</code>)
 */
class Operators {

	@Category(Term)
	class TermOperators {

		/**
		 * Binds an error message to a current subrule. 
		 */
		Subrule getAt(String message) {
			SubrulesFactory.create(this, new ValidationErrorProperties(message))
		}
		
		/**
 		 * Binds a redirect URL to a current subrule.
		 */
		Subrule getAt(ValidationErrorProperties errorProperties) {
			SubrulesFactory.create(this, errorProperties)
		}
		
		/**
		 * Creates a validation term that is a conjunction of two other terms.
		 */
    AndTerm and(expression) {
		  Term term = TermWrapper.wrap(expression)
		  new AndTerm(this, term)
	  }
		
		/**
		 * This method is added to produce an exception when a conjunction is applied to a conversion term.
		 */
		AndTerm and(TildeTerm expression) {
			throw new InvalidBooleanTermException(this, expression, Types.LOGICAL_AND)
		}

	  /**
		 * Creates a validation term that is a disjunction of two other terms.
		 */
	  OrTerm or(expression) {
		  Term term = TermWrapper.wrap(expression)
		  new OrTerm(this, term)
	  }
		
		/**
		 * This method is added to produce an exception when a disjunction is applied to a conversion term.
		 */
		AndTerm or(TildeTerm expression) {
			throw new InvalidBooleanTermException(this, expression, Types.LOGICAL_OR)
		}
	 
	  /**
		 * Creates a validation term that is a negation of itself.
		 */
	  NotTerm negative() {
		  new NotTerm(this)
	  }
		
		/**
		 * Creates a conversion term.
		 */
		TildeTerm bitwiseNegate() {
			new TildeTerm(this)
		}
	}


	@Category(TildeTerm)
	class TildeTermOperators {

		/**
		 * This method is added to produce an exception when a conjunction is applied to a conversion term. 
		 */
		AndTerm and(expression) {
			throw new InvalidBooleanTermException(expression)
		}

		/**
		 * This method is added to produce an exception when a disjunction is applied to a conversion term.
		 */
		AndTerm or(expression) {
			throw new InvalidBooleanTermException(expression)
		}
		
		/**
		 * This method is added to produce an exception when a negation is applied to a conversion term.
		 */
		NotTerm negative() {
			throw new InvalidBooleanTermException(this, Types.NOT)
		}
	}

	@Category(Closure)
	class ClosureOperators {

		/**
		 * Creates a validation term that is a conjunction of validation closure and the given term.
		 */
		AndTerm and(rightTerm) {
			(new ClosureTerm(this) as Term) & rightTerm
		}

		/**
		 * Creates a validation term that is a disjunction of validation closure and the given term.
		 */
		OrTerm or(rightTerm) {
			(new ClosureTerm(this) as Term) | rightTerm
		}
		
		/**
		 * Creates a validation term that is a negation of the given closure.
		 */
	  NotTerm negative() {
		  new NotTerm(this)
	  }

		/**
 		 * Binds an error message to a subrule implemented as a closure.
	 	 */
		Subrule getAt(String message) {
			SubrulesFactory.create(new ClosureTerm(this), new ValidationErrorProperties(message))
		}
		
	  /**
		 * Binds a redirect URL to a subrule implemented as a closure.
		 */
		Subrule getAt(ValidationErrorProperties errorProperties) {
			SubrulesFactory.create(new ClosureTerm(this), errorProperties)
		}
		
		/**
		 * Creates a conversion term.
		 */
		TildeTerm bitwiseNegate() {
			new TildeTerm(this)
		}

	}

	/**
	 * The ">>" operator that combines two subrules in a sequence such that a result of application of a first subrule is 
	 * passed to a second subrule.   
	 */
	@Category(SubrulesSeq)
	class SubrulesSeqOperators {

		SubrulesSeq rightShift(exression) {
			(this as SubrulesSeq).add(exression)
		}
	}
}