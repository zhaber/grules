package org.grules.functions.lib

import static org.grules.TestScriptEntities.*

import org.grules.ValidationException

import spock.lang.Specification

class MathFunctionsTest extends Specification {
	
	MathFunctions mathFunctions = new MathFunctions()
	
	def "abs"() {
		expect:
		  mathFunctions.abs(-1) == 1 
	}
	
	def "add"() {
		expect:
			mathFunctions.add(1, 2) == 3
	}
  
  def "ceil"() {
    expect:
      mathFunctions.ceil(0.1) == 1
  }

	def "div"() {
		expect:
			mathFunctions.div(0.2, 2) == 0.1
	}
	
	def "division by zero"() {
		when:
			mathFunctions.div(1, 0)
		then:
		  thrown(ValidationException)
	}
  
  def "floor"() {
    expect:
      mathFunctions.floor(0.1) == 0
  }
  
	def "isEven"() {
		expect:
			!mathFunctions.isEven(1)
			mathFunctions.isEven(0)
			!mathFunctions.isEven(-1)
	}
  
	def "isOdd"() {
		expect:
			mathFunctions.isOdd(1)
			!mathFunctions.isOdd(0)
			mathFunctions.isOdd(-1)
	}

  def "minus"() {
    expect:
      mathFunctions.minus(5, 3) == 2
  }
  
 	def "mod"() {
		expect:
			mathFunctions.mod(5, 2) == 1
	}
	
	def "mult"() {
		expect:
			mathFunctions.mult(0.1, 2) == 0.2
	}

	def "pow"() {
		expect:
		  mathFunctions.pow(0.5, 2) == 0.25
	}
	
	def "round"() {
		expect:
		  mathFunctions.round(0.8) == 1
	}
}