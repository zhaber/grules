package org.grules.script

/**
 * Signals that a variable was not declared as a parameter before use. 
 */
class UnprocessedParameterException extends Exception {

	UnprocessedParameterException(String name) {
		super("No rule was applied to $name parameter.")
	}
}