package org.grules.script.expressions

interface Term {

	/**
	 * Applies a term to the given value.
	 * 
	 * @param value processed parameter or any other type of value
	 * @return result of term application
	 */
	def apply(value)
}