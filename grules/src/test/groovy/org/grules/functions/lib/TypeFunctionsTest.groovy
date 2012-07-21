package org.grules.functions.lib

import static org.grules.TestScriptEntities.*

import org.grules.ValidationException
import org.grules.functions.ConverterBooleanResult
import org.joda.time.DateTime
import spock.lang.Specification

enum TestEnum {
	ELEMENT
}

class TypeFunctionsTest extends Specification {
	
	TypeFunctions typeFunctions = new TypeFunctions()
	
	DateTime nowDateTime = DateTime.now()
	static final POSITIVE_LONG = Long.MAX_VALUE
	static final POSITIVE_INTEGER = 1
	static final POSITIVE_BIGDECIMAL = 1.1
	static final POSITIVE_BIGDECIMAL_STRING = POSITIVE_BIGDECIMAL.toString()
	static final BIGDECIMAL_STRING = POSITIVE_BIGDECIMAL_STRING
	static final NEGATIVE_BIGDECIMAL = -POSITIVE_BIGDECIMAL
	static final INTEGER = VALID_INTEGER
	static final LONG = POSITIVE_LONG
	static final NEGATIVE_INTEGER = -POSITIVE_INTEGER
	static final INTEGER_STRING = INTEGER.toString()
	static final POSITIVE_INTEGER_STRING = INTEGER_STRING
	static final NEGATIVE_INTEGER_STRING = '-' + POSITIVE_INTEGER_STRING
	static final NEGATIVE_BIGDECIMAL_STRING = '-' + POSITIVE_BIGDECIMAL_STRING
	static final POSITIVE_LONG_STRING = POSITIVE_LONG.toString()
	static final NEGATIVE_LONG_STRING = NEGATIVE_LONG.toString()
	static final NEGATIVE_LONG = Long.MIN_VALUE
	static final LONG_STRING = POSITIVE_LONG_STRING
	static final ZERO_STRING = '0'
	static final BigDecimal BIGDECIMAL = POSITIVE_BIGDECIMAL

	def "isNonnegative"() {
		expect:
		  typeFunctions.isNonnegative(POSITIVE_INTEGER)
			typeFunctions.isNonnegative(0)
      !typeFunctions.isNonnegative(NEGATIVE_INTEGER)
	}
  
  def "isPositive"() {
    expect:
      typeFunctions.isPositive(POSITIVE_INTEGER)
      !typeFunctions.isPositive(0)
      !typeFunctions.isPositive(NEGATIVE_INTEGER)
  }

	def "toBigDecimal"() {
		expect:
			typeFunctions.toBigDecimal(BIGDECIMAL_STRING) == BIGDECIMAL
	}
	
	def "toBoolean"() {
		expect:
			!(typeFunctions.toBoolean('') as ConverterBooleanResult).value
			(typeFunctions.toBoolean(ZERO_STRING) as ConverterBooleanResult).value
			!(typeFunctions.toBoolean(0) as ConverterBooleanResult).value
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
  	
	def "toDouble"() {
		expect:
			typeFunctions.toDouble(BIGDECIMAL_STRING) == BIGDECIMAL.doubleValue()
	}
  
	def "toEnum"() {
		expect:
		  typeFunctions.toEnum(TestEnum.ELEMENT.name(), TestEnum) == TestEnum.ELEMENT
	}
	
	def "toEnum invalid value"() {
		when:
			typeFunctions.toEnum('invalidElement', TestEnum)
		then:
		  thrown(ValidationException)
	}
  
  def "toFloat"() {
    expect:
      typeFunctions.toFloat(BIGDECIMAL_STRING) == BIGDECIMAL.floatValue()
  }

	def "toNonnegativeBigDecimal"() {
		when:
			typeFunctions.toNonnegativeBigDecimal(NEGATIVE_BIGDECIMAL_STRING)
		then:
		  thrown(ValidationException)
	}
	
	def "toNonnegativeBigDecimal for zero"() {
		when:
			typeFunctions.toNonnegativeBigDecimal(ZERO_STRING)
		then:
			notThrown(ValidationException)
	}
  
  def "toNonnegativeDouble"() {
    when:
      typeFunctions.toNonnegativeDouble(NEGATIVE_BIGDECIMAL_STRING)
    then:
      thrown(ValidationException)
  }
  
  def "toNonnegativeFloat"() {
    when:
      typeFunctions.toNonnegativeFloat(NEGATIVE_BIGDECIMAL_STRING)
    then:
      thrown(ValidationException)
  }

	def "toPositiveBigDecimal"() {
		when:
			typeFunctions.toPositiveBigDecimal(ZERO_STRING)
		then:
		  thrown(ValidationException)
	}
  
  def "toPositiveDouble"() {
    when:
      typeFunctions.toPositiveDouble(ZERO_STRING)
    then:
      thrown(ValidationException)
  }
  
  def "toPositiveFloat"() {
    when:
      typeFunctions.toPositiveFloat(ZERO_STRING)
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