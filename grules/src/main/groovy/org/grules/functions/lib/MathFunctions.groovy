package org.grules.functions.lib

import org.grules.ValidationException
import org.grules.functions.Functions

/**
 * Standard grules converters and validators.
 */
@Functions
class MathFunctions {

  /**
   * Returns the absolute value of a value.
   */
  Integer abs(Integer value) {
    Integer result = Math.abs(value)
    if (result < 0) {
      throw new NumberFormatException()
    }
    result
  }

  /**
   * Returns the absolute value of a value.
   */
  Long abs(Long value) {
    Long result = Math.abs(value)
    if (result < 0) {
      throw new NumberFormatException()
    }
    result
  }

  /**
   * Returns the absolute value of a value.
   */
  Float abs(Float value) {
    Float result = Math.abs(value)
    if (result < 0) {
      throw new NumberFormatException()
    }
    result
  }

  /**
   * Returns the absolute value of a value.
   */
  Double abs(Double value) {
    Double result = Math.abs(value)
    if (result < 0) {
      throw new NumberFormatException()
    }
    result
  }

  /**
   * Adds the specified number to a value.
   */
  Number add(Number value, Number number) {
    value + number
  }

  /**
   * Returns the smallest (closest to negative infinity) {@code double} value that is greater than or equal to the
   * argument and is equal to a mathematical integer.
   */
  Double ceil(Double value) {
    Math.ceil(value)
  }

  /**
   * Divides a value by the given number.
   */
  Number div(Number value, Number number) {
    if (number == 0) {
      throw new ValidationException('Division by zero')
    }
    value / number
  }

  /**
   * Returns the largest (closest to positive infinity) {@code double} value that is less than or equal to the
   * argument and is equal to a mathematical integer.
   */
  Double floor(Double value) {
    Math.floor(value)
  }

  /**
   * Checks that a value falls within the specified range of decimal values.
   */
  boolean isBetween(Number value, Number min, Number max) {
    isGreaterEq(value, min) && isLessEq(value, max)
  }

  /**
   * Checks that a value falls within the specified integer range.
   */
  boolean isBetween(Integer value, IntRange range) {
    value in range
  }

  /**
   * Checks that a value is even.
   */
  boolean isEven(Long value) {
    !isOdd(value)
  }

  /**
   * Checks that a value is greater than the specified number.
   */
  boolean isGreater(Number value, Number number) {
    value > number
  }

  /**
   * @see isGreater
   */
  boolean gt(Number value, Number number) {
    isGreater(value, number)
  }

  /**
   * Checks that a value is greater or equal to the specified number.
   */
  boolean isGreaterEq(Number value, Number number) {
    value >= number
  }

  /**
   * @see isGreaterEq
   */
  boolean gte(Number value, Number number) {
    isGreaterEq(value, number)
  }

  /**
   * Checks that a value is less than the specified number.
   */
  boolean isLess(Number value, Number number) {
    value < number
  }

  /**
   * @see isLess
   */
  boolean lt(Number value, Number number) {
    isLess(value, number)
  }

  /**
   * Checks that a value is less or equal to the specified number.
   */
  boolean isLessEq(Number value, Number number) {
    value <= number
  }

  /**
   * Checks that a value is a positive number.
   */
  boolean isPositive(Number value) {
    value > 0
  }

  /**
   * Checks that a value is a nonnegative number.
   */
  boolean isNonnegative(Number value) {
    value >= 0
  }

  /**
   * @see isLessEq
   */
  boolean lte(Number value, Number number) {
    isLessEq(value, number)
  }

  /**
   * Checks that a value is odd.
   */
  boolean isOdd(Long value) {
    (value & 1) == 1
  }

  /**
   * Returns a value modulo <code>number</code>.
   */
  Number mod(Long value, Long number) {
    value % number
  }

  /**
   * Substracts <code>number</code> from a value (same as <code>{it + number}</code>)
   */
  Number minus(Number value, Number number) {
    value - number
  }

  /**
   * Multiplies a value by <code>number</code>.
   */
  Number mult(Number value, Number number) {
    value * number
  }

  /**
   * Returns a value raised to the power <code>exponent</code>.
   */
  Double pow(Double value, Double exponent) {
    value ** exponent
  }

  /**
   * Returns the closest {@code long} to the argument, with ties rounding up.
   */
  Long round(Double value) {
    Math.round(value)
  }

}
