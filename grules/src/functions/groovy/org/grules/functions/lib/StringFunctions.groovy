package org.grules.functions.lib

import org.grules.functions.Functions

/**
 * Converters and validators for strings.
 */
@Functions
class StringFunctions {

  /**
   * Capitalizes the first letter.
   */
  String capitalize(String value) {
    //Groovy 2.0: StringGroovyMethods.capitalize(value)
    value.capitalize()
  }

  /**
   * Checks that a value contains the specified sequence of char values.
   */
  boolean contains(String value, CharSequence substring) {
    value.contains(substring)
  }

  /**
   * @see {@link String#format(String, Object...)}
   */
  String format(String value, Object... args) {
    String.format(value, args)
  }

  /**
   * Checks that a value ends with the specified suffix.
   */
  boolean endsWith(String value, String suffix) {
    value.endsWith(suffix)
  }

  /**
   * Checks that a string matches <code>/[a-zA-Z]+/</code>.
   */
  boolean isAlpha(String value) {
    value ==~ /[a-zA-Z]+/
  }

  /**
   * Checks that a string matches <code>/[a-zA-Z\s]+/</code>.
   */
  boolean isAlphaSpace(String value) {
    value ==~ /[a-zA-Z\s]+/
  }

  /**
   * Checks that a string matches <code>/[a-zA-Z0-9]+/</code>.
   */
  boolean isAlphanum(String value) {
    value ==~ /[a-zA-Z0-9]+/
  }

  /**
   * Checks that a string matches <code>/[a-zA-Z0-9\s]+/</code>.
   */
  boolean isAlphanumSpace(String value) {
    value ==~ /[a-zA-Z0-9\s]+/
  }

  /**
   * Checks that a value has the specified string length.
   */
  boolean isLengthEq(String value, Integer length) {
    value.length() == length
  }

  /**
   * Checks that a value string length is within the specified range (the range is inclusive).
   */
  boolean isLengthBetween(String value, Integer minLength, Integer maxLength) {
    value.length() >= minLength && value.length() <= maxLength
  }

  /**
   * Checks that a value has a string length less than the specified length.
   */
  boolean isLengthLess(String value, Integer maxLength) {
    value.length() < maxLength
  }

  /**
   * Checks that a value has a string length more than the specified length.
   */
  boolean isLengthMore(String value, Integer minLength) {
    value.length() > minLength
  }

  /**
   * Checks that a value has a string length less or equal to specified length.
   */
  boolean isLengthLessEq(String value, Integer maxLength) {
    value.length() <= maxLength
  }

  /**
   * Checks that a value has a string length more or equal to specified length.
   */
  boolean isLengthMoreEq(String value, Integer minLength) {
    value.length() >= minLength
  }

  /**
   * Checks that a value is a string that matches the specified regex.
   */
  boolean matches(String value, String regex) {
    value ==~ regex
  }

  /**
   * Replaces each substring of a string value that matches the given regular expression with the given replacement.
   */
  String replaceAll(String value, String regexp, String replacement) {
    value.replaceAll(regexp, replacement)
  }

  /**
   * Returns a new string that is a substring of this string.
   */
  String substring(String value, Integer beginIndex, Integer endIndex = -1) {
    value[beginIndex..endIndex]
  }

  /**
   * Checks if a value starts with the specified prefix.
   */
  boolean startsWith(String value, String prefix) {
    value.startsWith(prefix)
  }

  /**
   * Converts all of the characters in a value to lower case using the rules of the default locale.
   */
  String toLowerCase(String value) {
    value.toLowerCase()
  }

  /**
   * Converts all of the characters in a value to upper case using the rules of the default locale.
   */
  String toUpperCase(String value) {
    value.toUpperCase()
  }

  /**
   * Returns a copy of a value, with leading and trailing whitespace omitted.
   */
  String trim(String value) {
    value.trim()
  }

  /**
   * An additional trim method to make it usable as a default converter.
   */
  def trim(value) {
    value
  }
}

