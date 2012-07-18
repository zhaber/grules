package org.grules.ast

import static org.grules.TestScriptEntities.*

import org.grules.functions.ConverterBooleanResult
import org.grules.functions.lib.TypeFunctions

import spock.lang.Specification

class ConverterASTTransformationTest extends Specification {
	
	def "Boolean coverter returns wrapped boolean"() {
		expect:
		  (new TypeFunctions()).toBoolean(true) instanceof ConverterBooleanResult
	}
	
}