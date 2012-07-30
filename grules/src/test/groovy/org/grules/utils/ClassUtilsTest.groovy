package org.grules.utils

import spock.lang.Specification

class ClassUtilsTest extends Specification {

	def "hasMixin"() {
	setup:
    def obj = new Object()
    obj.metaClass.mixin(String)
	expect:
	  !ClassUtils.hasMixin(new Object(), String)
    ClassUtils.hasMixin(obj, String)
    !ClassUtils.hasMixin(obj, Integer)
	}
}
