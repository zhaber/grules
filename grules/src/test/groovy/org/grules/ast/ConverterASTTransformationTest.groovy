package org.grules.ast

import static org.grules.TestScriptEntities.*

import org.grules.functions.ConverterBooleanResult
import org.grules.functions.lib.TypeFunctions

import spock.lang.Specification

class ConverterASTTransformationTest extends Specification {

	def "Boolean coverter returns boolean"() {
    setup:
      def booleanResult = (new TypeFunctions()).toBoolean(true)
		expect:
		   booleanResult instanceof Boolean
	}

  def "ConverterBooleanResult is mixed in with ConverterBooleanResult"() {
    setup:
      def booleanResult = (new TypeFunctions()).toBoolean(true)
    when:
      booleanResult as ConverterBooleanResult
    then:
      notThrown(ClassCastException)
  }

}