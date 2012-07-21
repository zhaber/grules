package org.grules.functions.lib

import spock.lang.Specification

class StringFunctionsTest extends Specification {

	static final String LOWERCASE_STRING = 'a'
	static final String UPPERCASE_STRING = LOWERCASE_STRING.toUpperCase()
	static final String SUFFIX = 's'
	static final String PREFIX = 'p'
	static final String STRING = PREFIX + SUFFIX
	static final String ALPHA_STRING = STRING
	static final String INTEGER_STRING = '1'
	static final String ALPHANUM_STRING = STRING + INTEGER_STRING
	static final String SUB_STRING = SUFFIX
	
	StringFunctions stringFunctions = new StringFunctions()
	
	def "capitalize"() {
		expect:
		  stringFunctions.capitalize(LOWERCASE_STRING + SUFFIX) == UPPERCASE_STRING + SUFFIX 
	}

	def "contains"() {
		expect:
		  stringFunctions.contains(STRING, SUB_STRING)
	}

	def "endsWith"() {
		expect:
		  stringFunctions.endsWith(STRING, SUFFIX)
	}

	def "isAlpha"() {
		expect:
		  stringFunctions.isAlpha(ALPHA_STRING)
			!stringFunctions.isAlpha(ALPHANUM_STRING)
	}

	def "isAlphanum"() {
		expect:
			stringFunctions.isAlphanum(ALPHANUM_STRING)
	}

	def "isLengthEq"() {
		expect:
		  stringFunctions.isLengthEq(STRING, STRING.size())
	}

	def "isLengthBetween"() {
		expect:
		  stringFunctions.isLengthBetween(STRING, STRING.size() - 1, STRING.size())
			stringFunctions.isLengthBetween(STRING, STRING.size() - 1, STRING.size() + 1)
			!stringFunctions.isLengthBetween(STRING, STRING.size() - 1, STRING.size() - 1)
	}

	def "isLengthLess"() {
		expect:
		  stringFunctions.isLengthLess(STRING, STRING.size() + 1)
			!stringFunctions.isLengthLess(STRING, STRING.size())
	}

	def "isLengthMore"() {
		expect:
		  stringFunctions.isLengthMore(STRING, STRING.size() - 1)
			!stringFunctions.isLengthMore(STRING, STRING.size())
	}

	def "isLengthLessEq"() {
		expect:
		  stringFunctions.isLengthLessEq(STRING, STRING.size() + 1)
			stringFunctions.isLengthLessEq(STRING, STRING.size())
			!stringFunctions.isLengthLessEq(STRING, STRING.size() - 1)
	}

	def "isLengthMoreEq"() {
		expect:
		  stringFunctions.isLengthMoreEq(STRING, STRING.size() - 1)
			stringFunctions.isLengthMoreEq(STRING, STRING.size())
			!stringFunctions.isLengthMoreEq(STRING, STRING.size() + 1)
	}

	def "matches"() {
		expect:
		  stringFunctions.matches(STRING, '.*')
	}
	
	def "replace"() {
		expect:
		  stringFunctions.replace(STRING, SUFFIX, '') == PREFIX
	}

	def "substring"() {
		expect:
		  stringFunctions.substring(STRING, 0, PREFIX.size() - 1) == PREFIX
	}

	def "startsWith"() {
		expect:
		  stringFunctions.startsWith(STRING, PREFIX)
	}
	
	def "toLowerCase"() {
		expect:
		stringFunctions.toLowerCase(UPPERCASE_STRING)
	}

	def "toUpperCase"() {
		expect:
		  stringFunctions.toUpperCase(LOWERCASE_STRING) == UPPERCASE_STRING
	}

	def "trim"() {
		expect:
		  stringFunctions.trim(' ') == ''
	} 
}