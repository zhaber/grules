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

  def "isBetween"() {
    expect:
      mathFunctions.isBetween(1, 1, 3)
      mathFunctions.isBetween(1.1, 1, 3)
      mathFunctions.isBetween(1, 0, 3)
      !mathFunctions.isBetween(1, 2, 3)
      mathFunctions.isBetween(1, 1..3)
      mathFunctions.isBetween(1, 0..3)
      !mathFunctions.isBetween(1, 2..3)
  }

	def "isEven"() {
		expect:
			!mathFunctions.isEven(1)
			mathFunctions.isEven(0)
			!mathFunctions.isEven(-1)
	}

  def "isLess"() {
    expect:
      mathFunctions.isLess(1, 2)
      !mathFunctions.isLess(1, 1)
      !mathFunctions.isLess(2, 1)
  }

  def "isLessEq"() {
    expect:
      mathFunctions.isLessEq(1, 2)
      mathFunctions.isLessEq(1, 1)
      !mathFunctions.isLessEq(2, 1)
  }

  def "isGreater"() {
    expect:
      !mathFunctions.isGreater(1, 2)
      !mathFunctions.isGreater(1, 1)
      mathFunctions.isGreater(2, 1)
  }

  def "isGreaterEq"() {
    expect:
      !mathFunctions.isGreaterEq(1, 2)
      mathFunctions.isGreaterEq(1, 1)
      mathFunctions.isGreaterEq(2, 1)
  }

  def "isNonnegative"() {
    expect:
      mathFunctions.isNonnegative(1)
      mathFunctions.isNonnegative(0)
      !mathFunctions.isNonnegative(-1)
  }

	def "isOdd"() {
		expect:
			mathFunctions.isOdd(1)
			!mathFunctions.isOdd(0)
			mathFunctions.isOdd(-1)
	}

  def "isPositive"() {
    expect:
      mathFunctions.isPositive(1)
      !mathFunctions.isPositive(0)
      !mathFunctions.isPositive(-1)
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