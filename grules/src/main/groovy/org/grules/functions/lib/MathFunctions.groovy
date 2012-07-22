package org.grules.functions.lib

import org.grules.ValidationException
import org.grules.ast.Functions

/**
 * Standard grules converters and validators.
 */
@Functions
class MathFunctions {

  Integer abs(Integer value) {
    Integer result = Math.abs(value)
    if (result < 0) {
      throw new NumberFormatException()
    }
    result
  }

  Long abs(Long value) {
    Long result = Math.abs(value)
    if (result < 0) {
      throw new NumberFormatException()
    }
    result
  }

  Float abs(Float value) {
    Float result = Math.abs(value)
    if (result < 0) {
      throw new NumberFormatException()
    }
    result
  }

  Double abs(Double value) {
    Double result = Math.abs(value)
    if (result < 0) {
      throw new NumberFormatException()
    }
    result
  }

  Number add(Number value, Number number) {
    value + number
  }

  Double ceil(Double value) {
    Math.ceil(value)
  }

  Number div(Number value, Number number) {
    if (number == 0) {
      throw new ValidationException('Division by zero')
    }
    value / number
  }

  Double floor(Double value) {
    Math.floor(value)
  }

  boolean isEven(Integer value) {
    !isOdd(value)
  }

  boolean isOdd(Integer value) {
    (value & 1) == 1
  }

  Number mod(Long value, Long number) {
    value % number
  }

  Number minus(Number value, Number number) {
    value - number
  }

  Number mult(Number value, Number number) {
    value * number
  }

  Double pow(Double value, Double number) {
    Math.pow(value, number)
  }

  Double round(Double value) {
    Math.round(value)
  }

}
