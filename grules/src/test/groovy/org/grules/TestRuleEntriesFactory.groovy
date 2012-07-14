package org.grules

import org.grules.script.expressions.ClosureTerm
import org.grules.script.expressions.TildeTerm
import org.grules.script.expressions.Subrule
import org.grules.script.expressions.SubrulesFactory
import org.grules.script.expressions.SubrulesSeq
import org.grules.script.expressions.Term


class TestRuleEntriesFactory {

	static Closure<SubrulesSeq> newEmptyRuleClosure() {
		{->new SubrulesSeq()}
	}

	static Term newTerm() {
		{value -> value} as Term
	}
	
	static Term newConversionTerm() {
		{value -> value} as Term
	}
	
	static Term newValidationTerm() {
		{value -> true} as Term
	}
	
	static Term newFailValidationTerm() {
		{value -> false} as Term
	}
	
	static Term newSuccessValidationTerm() {
		{value -> true} as Term
	}

	static TildeTerm newTildeTerm() {
		new TildeTerm(newTerm())
	}
		
	static ClosureTerm newIsIntegerValidator() {
		new ClosureTerm({String number -> number.isInteger()})
	}
	
	static ClosureTerm newIsEmptyValidator() {
		new ClosureTerm({String term -> term.isEmpty()})
	}
	
	static ClosureTerm newValidatorClosureTerm() {
		new ClosureTerm({})
	}
	
	static TildeTerm newTrimConverter() {  
	  new TildeTerm({String string -> string.trim()})
	}
	
	static TildeTerm newToIntConverter() {
		new TildeTerm({String string ->
			try { 
			  string.toInteger()
			} catch(NumberFormatException e) {
			  throw new ValidationException()
			}
		})
	}
	
	static SubrulesSeq newSubrulesSeq() {
		(new SubrulesSeq()).add(newTrimConverter())
	}
	
	static Subrule newSubrule() {
		SubrulesFactory.create(newTrimConverter())
	}
}