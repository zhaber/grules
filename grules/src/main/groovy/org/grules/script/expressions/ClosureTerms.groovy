package org.grules.script.expressions


/**
 * A term implemented as a closure. 
 */
class ClosureTerm implements Term {

	private final Closure closure

	ClosureTerm(Closure closure) {
		this.closure = closure
	}
	
	/**
	 * Returns a result of closure application.
	 */
	@Override
	def apply(value) {
		closure.call(value)
	}
}