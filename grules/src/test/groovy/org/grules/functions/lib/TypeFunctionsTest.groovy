package org.grules.functions.lib

import static org.grules.TestScriptEntities.VALID_INTEGER

import org.grules.ValidationException
import org.joda.time.DateTime

import spock.lang.Specification

enum TestEnum {
  ELEMENT
}

class TypeFunctionsTest extends Specification {

  private final TypeFunctions typeFunctions = new TypeFunctions()

  private final DateTime nowDateTime = DateTime.now()
  private static final POSITIVE_LONG = Long.MAX_VALUE
  private static final POSITIVE_BIGDECIMAL = 1.1
  private static final POSITIVE_BIGDECIMAL_STRING = POSITIVE_BIGDECIMAL.toString()
  private static final BIGDECIMAL_STRING = POSITIVE_BIGDECIMAL_STRING
  private static final INTEGER = VALID_INTEGER
  private static final LONG = POSITIVE_LONG
  private static final INTEGER_STRING = INTEGER.toString()
  private static final INVALID_INTEGER_STRING = INTEGER_STRING + '?'
  private static final POSITIVE_INTEGER_STRING = INTEGER_STRING
  private static final NEGATIVE_INTEGER_STRING = '-' + POSITIVE_INTEGER_STRING
  private static final NEGATIVE_BIGDECIMAL_STRING = '-' + POSITIVE_BIGDECIMAL_STRING
  private static final POSITIVE_LONG_STRING = POSITIVE_LONG.toString()
  private static final NEGATIVE_LONG_STRING = NEGATIVE_LONG.toString()
  private static final NEGATIVE_LONG = Long.MIN_VALUE
  private static final LONG_STRING = POSITIVE_LONG_STRING
  private static final ZERO_STRING = '0'
  private static final BigDecimal BIGDECIMAL = POSITIVE_BIGDECIMAL

  def "toBigDecimal"() {
    expect:
      typeFunctions.toBigDecimal(BIGDECIMAL_STRING) == BIGDECIMAL
  }

  def "Validation exception internal error"() {
    when:
      typeFunctions.toInt(INVALID_INTEGER_STRING)
    then:
      ValidationException e = thrown(ValidationException)
    expect:
      e.errorProperties.exception instanceof NumberFormatException
  }

  def "toBoolean"() {
    expect:
      !typeFunctions.toBoolean('')
      typeFunctions.toBoolean(ZERO_STRING)
      !typeFunctions.toBoolean(0)
  }

  def "toBooleanList"() {
    setup:
      def booleanList = ['', 1]
    expect:
      typeFunctions.toBooleanList(booleanList).first() instanceof Boolean
      typeFunctions.toBooleanList(booleanList) == [false, true]
  }

  def "toChar"() {
    expect:
      typeFunctions.toChar('ab') == 'a' as char
  }

  def "toChar invalid"() {
    when:
      typeFunctions.toChar('')
    then:
      thrown(ValidationException)
  }

  def "toCharList"() {
    expect:
      typeFunctions.toCharList(['ab', 'c']) == ['a' as char, 'c' as char]
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
      typeFunctions.toDouble(BIGDECIMAL_STRING) instanceof Double
      typeFunctions.toDouble(BIGDECIMAL_STRING) == BIGDECIMAL.doubleValue()
  }

  def "toDoubleList"() {
    expect:
      typeFunctions.toDoubleList([BIGDECIMAL_STRING]).first() instanceof Double
      typeFunctions.toDoubleList([BIGDECIMAL_STRING]) == [BIGDECIMAL.doubleValue()]
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
      typeFunctions.toFloat(BIGDECIMAL_STRING) instanceof Float
      typeFunctions.toFloat(BIGDECIMAL_STRING) == BIGDECIMAL.floatValue()
  }

  def "toFloatList"() {
    expect:
      typeFunctions.toFloatList([BIGDECIMAL_STRING]).first() instanceof Float
      typeFunctions.toFloatList([BIGDECIMAL_STRING]) == [BIGDECIMAL.floatValue()]
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
      typeFunctions.toLong(LONG_STRING) instanceof Long
      typeFunctions.toLong(LONG_STRING) == LONG
  }

  def "toLongList"() {
    expect:
      typeFunctions.toLongList([LONG_STRING]).first() instanceof Long
      typeFunctions.toLongList([LONG_STRING]) == [LONG]
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
      typeFunctions.toInt(INTEGER_STRING) instanceof Integer
      typeFunctions.toInt(INTEGER_STRING) == INTEGER
  }

  def "toIntList"() {
    expect:
      typeFunctions.toIntList([INTEGER_STRING]).first() instanceof Integer
      typeFunctions.toIntList([INTEGER_STRING]) == [INTEGER]
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
