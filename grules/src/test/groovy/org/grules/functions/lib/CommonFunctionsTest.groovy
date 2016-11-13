package org.grules.functions.lib

import static org.grules.TestScriptEntities.PARAMETER_VALUE
import static org.grules.TestScriptEntities.INVALID_PARAMETER_VALUE
import static org.grules.TestScriptEntities.PARAMETER_VALUE_AUX
import static org.grules.TestScriptEntities.JOIN_SEPARATOR

import spock.lang.Specification

class CommonFunctionsTest extends Specification {

  private final CommonFunctions commonFunctions = new CommonFunctions()

  def "areIn"() {
    expect:
      commonFunctions.areIn([PARAMETER_VALUE, PARAMETER_VALUE_AUX], [PARAMETER_VALUE, PARAMETER_VALUE_AUX, ''])
      !commonFunctions.areIn([PARAMETER_VALUE, PARAMETER_VALUE_AUX], [PARAMETER_VALUE])
  }

  def "decimalFormat"() {
    expect:
      commonFunctions.decimalFormat(1, '0.0') == '1.0'
  }

  def "eq"() {
    expect:
      commonFunctions.isEqual(PARAMETER_VALUE, PARAMETER_VALUE)
  }

  def "inverse"() {
    expect:
      commonFunctions.inverse(false)
  }

  def "isAny"() {
    expect:
      commonFunctions.isAny([PARAMETER_VALUE, INVALID_PARAMETER_VALUE]) { it == PARAMETER_VALUE }
      !commonFunctions.isAny([''])
  }

  def "isEmpty"() {
    expect:
      commonFunctions.isEmpty('')
      commonFunctions.isEmpty([])
  }

  def "isFalse"() {
    expect:
      commonFunctions.isFalse('')
  }

  def "isEvery"() {
    expect:
      commonFunctions.isEvery([PARAMETER_VALUE, PARAMETER_VALUE]) { it == PARAMETER_VALUE }
      !commonFunctions.isEvery([PARAMETER_VALUE, INVALID_PARAMETER_VALUE]) { it == PARAMETER_VALUE }
      !commonFunctions.isEvery([1, ''])
  }

  def "isIn"() {
    expect:
      commonFunctions.isIn(PARAMETER_VALUE, [PARAMETER_VALUE, INVALID_PARAMETER_VALUE])
  }

  def "join"() {
    setup:
      def joinResult = PARAMETER_VALUE + JOIN_SEPARATOR + PARAMETER_VALUE_AUX
    expect:
      commonFunctions.join([PARAMETER_VALUE, PARAMETER_VALUE_AUX], JOIN_SEPARATOR) == joinResult
  }

  def "nop"() {
    expect:
      commonFunctions.nop(PARAMETER_VALUE) == PARAMETER_VALUE
  }

  def "isTrue"() {
    expect:
      commonFunctions.isTrue(1)
  }
}
