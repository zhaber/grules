package org.grules.functions.lib

import org.grules.ast.Converter
import org.grules.ast.Functions

/**
 * Standard grules converters and validators.
 */
@Functions
class CommonFunctions {

  boolean eq(value1, value2) {
    value1 == value2
  }

  @Converter
  Boolean inverse(Boolean value) {
    !value
  }

  boolean isAny(List values, Closure validator) {
    values.any(validator)
  }

  boolean isFalse(value) {
    !value
  }

  boolean isEmpty(List value) {
    value.isEmpty()
  }

  boolean isEmpty(String value) {
    value.isEmpty()
  }

  boolean isEvery(List values, Closure validator) {
    values.every(validator)
  }

  boolean isIn(value, List objects) {
    value in objects
  }

  def nop(value) {
    value
  }

  boolean isTrue(value) {
    value
  }
}
