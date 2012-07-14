package org.grules.script

/**
 * Signals a cycle in rules script inclusion. 
 */
class CircularIncludeException extends Exception {

	CircularIncludeException(List<Class<? extends Script>> parentScripts) {
		super('Cyclic dependency detected in chain ' + parentScripts*.name)
	}
}
