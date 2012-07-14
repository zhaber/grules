package org.grules.script.expressions

import org.grules.ValidationErrorProperties
import org.grules.ValidationException

/**
 * A subrule expression (parts of a rule between ">>" operators). 
 */
class Subrule {

	private final Term term
	final ValidationErrorProperties errorProperties

	Subrule(Term term, ValidationErrorProperties errorProperties) {
		this.errorProperties = errorProperties
		this.term = term
	}
	
	/**
	 * Applies the subrule to the given value. 
	 */
	@Override
	def apply(value) {
		def applicationResult = term.apply(value)
		if (applicationResult instanceof Boolean && !(term instanceof TildeTerm)) {
			if (!applicationResult) {
			  throw new ValidationException()
			}
			value
		} else { 
			applicationResult
	  }
	}
}

/**
 * Factory for conversion and validation subrules.
 */
class SubrulesFactory {

	/**
	 * Creates a subrule based on the given term.
	 *
	 * @param term a term
	 * @param errorProperties error properties
	 * @return a subrule
	 */
  static Subrule create(Term term, ValidationErrorProperties errorProperties) {
 	  new Subrule(term, errorProperties)
  }
	
	/**
	 * Creates a subrule based on the given term.
	 *
	 * @param term a conversion or validation term
	 * @return a subrule
	 */
  static Subrule create(Term term) {
	  create(term, new ValidationErrorProperties())
  }
	
  /**
   * Creates a subrule by wrapping a closure to a validation term.
   * 
   * @param closure a validation closure
   * @return a subrule
   */
	static Subrule create(Closure closure) {
		create(new ClosureTerm(closure))
	}

	/**
	 * The method throws an error for unexpected subrule expression type.
	 */
	static Subrule create(expression) {
		throw new InvalidSubruleException(expression)
	}
}