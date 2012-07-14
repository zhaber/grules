package org.grules.utils

class SetUtils {
	
	/**
	 * Converts null to an empty set or returns the original set if it is not null.
   */
	static <T> Set<T> nullToEmpty(Set<T> set) {
		if (set != null) {
			set
		} else {
		  Collections.<T>emptySet()
		}
	}
}
