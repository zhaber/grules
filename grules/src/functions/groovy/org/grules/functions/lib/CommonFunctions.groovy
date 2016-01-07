package org.grules.functions.lib

import java.text.DecimalFormat

import org.grules.functions.Converter
import org.grules.functions.Functions

/**
 * Standard grules converters and validators.
 */
@Functions
class CommonFunctions {

  /**
   * Checks that all values are members of the list.
   */
  boolean areIn(List values, List list) {
    isEvery(values) { isIn(it, list) }
  }

  /**
   * Checks that all values are members of the set.
   */
  boolean areIn(List values, Set list) {
    isEvery(values) { isIn(it, list) }
  }

  /**
   * Iterates through this aggregate Object transforming each item into a new value using the <code>transform</code>
   * closure, returning a list of transformed values.
   *
   * @see {@link org.codehaus.groovy.runtime.DefaultGroovyMethods#collect(java.util.Collection, groovy.lang.Closure)}
   */
  List collect(List values, Closure closure) {
    // codenarc bug
    values.collect {
      closure(it)
    }
  }

  /**
   * @see {@link java.text.NumberFormat#format(double)}
   */
  String decimalFormat(Double value, String pattern) {
    DecimalFormat decimalFormat = new DecimalFormat(pattern)
    decimalFormat.format(value)
  }

  /**
   * @see {@link java.text.NumberFormat#format(long)}
   */
  String decimalFormat(Long value, String pattern) {
    DecimalFormat decimalFormat = new DecimalFormat(pattern)
    decimalFormat.format(value)
  }

  /**
   * @see isEqual
   */
  boolean eq(value, object) {
    isEqual(value, object)
  }

  /**
   * Returns an opposite value wrapped into {@link org.grules.functions.ConverterBooleanResult}: <code>true</code> for
   * <code>false</code> and <code>false</code> for <code>true</code>.
   */
  @Converter
  Boolean inverse(Boolean value) {
    !value
  }

  /**
   * Checks that at least one member is true according to the Groovy Truth
   */
  boolean isAny(List values) {
    values.any()
  }

  /**
   * Checks that at least one member is valid according to the predicate.
   */
  boolean isAny(List values, Closure validator) {
    values.any(validator)
  }

  /**
   * Checks that the list contains no members.
   */
  boolean isEmpty(List values) {
    values.isEmpty()
  }

  /**
   * Checks that the string matches <code>\/\s*\/</code>.
   */
  boolean isEmpty(String value) {
    value.isEmpty()
  }

  /**
   * Checks that a value is equal to the specified object.
   */
  boolean isEqual(value, object) {
    value == object
  }

  /**
   * Checks that all list members are valid according to the predicate.
   */
  boolean isEvery(List values, Closure validator) {
    values.every(validator)
  }

  /**
   * Checks that all list members are true according to the Groovy Truth.
   */
  boolean isEvery(List values) {
    values.every()
  }

  /**
   * Checks that a value coerces to boolean <code>false</code>.
   */
  boolean isFalse(value) {
    !value
  }

  /**
   * Checks that a value is equal to any member of the specified list.
   */
  boolean isIn(value, List list) {
    value in list
  }

  /**
   * Checks that a value is equal to any member of the specified set.
   */
  boolean isIn(value, Set set) {
    value in set
  }

  /**
   * Returns the passed value.
   */
  def nop(value) {
    value
  }

  /**
   * Checks that a value coerces to boolean <code>true</code>.
   */
  boolean isTrue(value) {
    value
  }

  /**
   * @see DefaultGroovyMethods#join(java.util.Collection, String)
   */
  String join(Collection<Object> values, String separator) {
    values.join(separator)
  }
}

