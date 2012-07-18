package org.grules.functions.lib

import static org.grules.TestScriptEntities.*

import org.grules.ValidationException
import org.grules.functions.ConverterBooleanResult
import org.joda.time.DateTime

import spock.lang.Specification

class TypeFunctionsTest extends Specification {
	
	TypeFunctions typeFunctions = new TypeFunctions()
	
	DateTime nowDateTime = DateTime.now()
	static final POSITIVE_LONG = Long.MAX_VALUE
	static final POSITIVE_INTEGER = 1
	static final POSITIVE_BIGDECIMAL = 1.1
	static final NEGATIVE_BIGDECIMAL = -POSITIVE_BIGDECIMAL
	static final INTEGER = VALID_INTEGER
	static final LONG = POSITIVE_LONG
	static final NEGATIVE_INTEGER = -POSITIVE_INTEGER
	static final INTEGER_STRING = INTEGER.toString()
	static final POSITIVE_INTEGER_STRING = INTEGER_STRING
	static final NEGATIVE_INTEGER_STRING = '-' + POSITIVE_INTEGER_STRING
	static final POSITIVE_BIGDECIMAL_STRING = POSITIVE_BIGDECIMAL.toString()
	static final NEGATIVE_BIGDECIMAL_STRING = '-' + POSITIVE_BIGDECIMAL_STRING
	static final BIGDECIMAL_STRING = POSITIVE_BIGDECIMAL_STRING
	static final POSITIVE_LONG_STRING = POSITIVE_LONG.toString()
	static final NEGATIVE_LONG_STRING = NEGATIVE_LONG.toString()
	static final NEGATIVE_LONG = Long.MIN_VALUE
	static final LONG_STRING = POSITIVE_LONG_STRING
	static final ZERO_STRING = '0'

	def "isBigDecimal"() {
		expect:
			typeFunctions.isBigDecimal(BIGDECIMAL_STRING)
	}
	
	def "toDate"() {
		setup:
			Date date = typeFunctions.toDate(nowDateTime.year.toString(), 'yyyy')
		expect:
			(new DateTime(date)).year == nowDateTime.year
	}

	def "toDate invalid"() {
		when:
			typeFunctions.toDate(nowDateTime.year.toString(), '*y')
		then:
			thrown(ValidationException)
	}
	
	def "isInt"() {
		expect:
			typeFunctions.isInt(INTEGER_STRING)
	}

	def "isLong"() {
		expect:
			typeFunctions.isLong(LONG_STRING)
	}
	
	def "isPositiveInt"() {
		expect:
		  typeFunctions.isPositiveInt(POSITIVE_INTEGER)
			!typeFunctions.isPositiveInt(NEGATIVE_INTEGER)
	}

	def "isPositiveBigDecimal"() {
		expect:
		  typeFunctions.isPositiveBigDecimal(POSITIVE_BIGDECIMAL)
			!typeFunctions.isPositiveBigDecimal(NEGATIVE_BIGDECIMAL)
	}

	def "isPositiveLong"() {
		expect:
			typeFunctions.isPositiveLong(POSITIVE_LONG)
			!typeFunctions.isPositiveLong(NEGATIVE_LONG)
	}
	
	def "toBigDecimal"() {
		expect:
			typeFunctions.toBigDecimal(POSITIVE_BIGDECIMAL_STRING) == POSITIVE_BIGDECIMAL
	}
	
	def "toBoolean"() {
		expect:
			!(typeFunctions.toBoolean('') as ConverterBooleanResult).value
			(typeFunctions.toBoolean(ZERO_STRING) as ConverterBooleanResult).value
			!(typeFunctions.toBoolean(0) as ConverterBooleanResult).value
	}

	def "toNaturalBigDecimal"() {
		when:
			typeFunctions.toNaturalBigDecimal(NEGATIVE_BIGDECIMAL_STRING)
		then:
		  thrown(ValidationException)
	}
	
	def "toNaturalBigDecimal for zero"() {
		when:
			typeFunctions.toNaturalBigDecimal(ZERO_STRING)
		then:
			notThrown(ValidationException)
	}

	def "toPositiveBigDecimal"() {
		when:
			typeFunctions.toPositiveBigDecimal(ZERO_STRING)
		then:
		  thrown(ValidationException)
	}

	def "toLong"() {
		expect:
			typeFunctions.toLong(LONG_STRING) == LONG
	}

	def "toNaturalLong"() {
		when:
			typeFunctions.toNaturalLong(NEGATIVE_LONG_STRING)
		then:
		  thrown(ValidationException)
	}
	
	def "toNaturalLong for zero"() {
		when:
			typeFunctions.toNaturalLong(ZERO_STRING)
		then:
			notThrown(ValidationException)
	}

	def "toPositiveLong"() {
		when:
			typeFunctions.toPositiveLong(ZERO_STRING)
		then:
		  thrown(ValidationException)
	}
	
	def "toInt"() {
		expect:
			typeFunctions.toLong(INTEGER_STRING) == INTEGER
	}

	def "toNaturalInt"() {
		when:
			typeFunctions.toNaturalInt(NEGATIVE_INTEGER_STRING)
		then:
			thrown(ValidationException)
	}
	
	def "toNaturalInt for zero"() {
		when:
			typeFunctions.toNaturalInt(ZERO_STRING)
		then:
			notThrown(ValidationException)
	}

	def "toPositiveInt"() {
		when:
			typeFunctions.toPositiveInt(ZERO_STRING)
		then:
			thrown(ValidationException)
	}
			
}