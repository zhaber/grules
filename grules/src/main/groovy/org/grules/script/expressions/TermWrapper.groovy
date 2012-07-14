package org.grules.script.expressions

/**
 * Wraps a closure into a term.
 */
class TermWrapper {

	/**
	 * Wraps a closure into a term.
	 *
	 * @param closure a validation closure
	 */
	static Term wrap(Closure closure) {
		wrap(new ClosureTerm(closure))
	}

	/**
	 * This method overloads other wrapper methods and immediately returns the given term.
	 *  
	 * @param term validation or conversion term
	 * @return the same term
	 */
	static Term wrap(Term term) {
		term
	}

	/**
	 * An unexpected expression type.
	 *
	 * @param rule expression
	 * @return throws the InvalidTermException exception
	 */
	static Term wrap(expression) {
		throw new InvalidTermException(expression)
	}
}