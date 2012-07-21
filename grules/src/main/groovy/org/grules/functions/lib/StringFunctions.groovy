package org.grules.functions.lib

import org.grules.ast.Functions

/**
 * Converters and validators for strings. 
 */
@Functions
class StringFunctions {
	
	String capitalize(String value) {
		//Groovy 2.0: StringGroovyMethods.capitalize(value) 
		value.capitalize()
	}

	boolean contains(String value, String substring) {
		value.contains(substring)
	}

	boolean endsWith(String value, String suffix) {
		value.endsWith(suffix)
	}

	boolean isAlpha(String value) {
		value ==~ /[a-zA-Z]*/
	}

	boolean isAlphanum(String value) {
		value ==~ /[a-zA-Z0-9]*/
	}

	boolean isLengthEq(String value, Integer length) {
		value.length() == length
	}

	boolean isLengthBetween(String value, Integer minLength, Integer maxLength) {
		value.length() >= minLength && value.length() <= maxLength
	}

	boolean isLengthLess(String value, Integer maxLength) {
		value.length() < maxLength
	}

	boolean isLengthMore(String value, Integer minLength) {
		value.length() > minLength
	}

	boolean isLengthLessEq(String value, Integer maxLength) {
		value.length() <= maxLength
	}

	boolean isLengthMoreEq(String value, Integer minLength) {
		value.length() >= minLength
	}

	boolean matches(String value, String regex) {
		value ==~ regex
	}
	
	String replace(String value, String regexp, String replacement) {
		value.replaceAll(regexp, replacement)
	}

	String substring(String value, Integer beginIndex, Integer endIndex) {
		value[beginIndex..endIndex]
	}

	boolean startsWith(String value, String prefix) {
		value.startsWith(prefix)
	}
	
	String toLowerCase(String value) {
		value.toLowerCase()
	}

	String toUpperCase(String value) {
		value.toUpperCase()
	}

	String trim(String value) {
		value.trim()
	} 
}