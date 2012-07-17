package org.grules.ast

import static org.grules.TestScriptEntities.*

import org.grules.functions.ConverterBooleanResult
import org.grules.functions.lib.CommonFunctions

import spock.lang.Specification

class ConverterASTTransformationTest extends Specification {
	
	CommonFunctions commonFuns = new CommonFunctions()

	def "Boolean coverter returns wrapped boolean"() {
		expect:
		  commonFuns.toBoolean(true) instanceof ConverterBooleanResult
	}
	
}