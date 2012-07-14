package org.grules.utils

class MapUtils {
	
	/**
 	 * Converts null to an empty map or returns the original map if it is not null.
	 */
	static <K,V> Map<K,V> nullToEmpty(Map<K,V> map) {
		if (map != null) {
			map
		} else {
		  Collections.<K,V>emptyMap()
		}
	}
}
