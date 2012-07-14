package org.grules.utils

import spock.lang.Specification

class MapUtilsTest extends Specification {

	def "nullToEmptyTest"() {
	setup:
	  def map = [:]
	expect:
		MapUtils.nullToEmpty(null) != null
		MapUtils.nullToEmpty(map) == map
	}
}
