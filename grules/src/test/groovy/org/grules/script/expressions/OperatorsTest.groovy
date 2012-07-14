package org.grules.script.expressions

import static org.grules.TestRuleEntriesFactory.*
import static org.grules.TestScriptEntities.*

import org.grules.script.expressions.Operators.ClosureOperators
import org.grules.script.expressions.Operators.TildeTermOperators
import org.grules.script.expressions.Operators.SubrulesSeqOperators
import org.grules.script.expressions.Operators.TermOperators

import spock.lang.Specification

class OperatorsTest extends Specification {

	def "AND operator throws exception for unknown term"() {
		when:
		  use(TermOperators) {
		    (newValidationTerm() & newTrimConverter()).apply('')
	    }
		then:
		  thrown(InvalidBooleanTermException)
	}
	
	def "OR operator throws exception for unknown term"() {
		when:
		  use(TermOperators) {
		    (newFailValidationTerm() | newTrimConverter()).apply('')
	    }
		then:
		  thrown(InvalidBooleanTermException)
	}
	
	def "AND operator throws exception if left term is tilde term"() {
		when:
		  use(TildeTermOperators){
  		  newTildeTerm() & newTerm()
	    }
		then:
		  thrown(InvalidBooleanTermException)
	}
	
	def "AND operator throws exception if right term is tilde term"() {
		when:
			use(TermOperators){
			  newValidationTerm() & newTildeTerm()
			}
		then:
			thrown(InvalidBooleanTermException)
	}
	
	def "OR operator throws exception if left term is tilde term"() {
		when:
		  use(TildeTermOperators){
  		  newTildeTerm() | newTerm()
	    }
	 	then:
		  thrown(InvalidBooleanTermException)
	}
	
	def "OR operator throws exception if right term is a tilde term"() {
		when:
			use(TermOperators) {
				newValidationTerm() | newTildeTerm()
			}
		then:
			thrown(InvalidBooleanTermException)
	}
	
	def "AND operator throws exception if left term returns non-boolean value"() {
		when:
			use(TermOperators){
				(newConversionTerm() & newValidationTerm()).apply('')
			}
		then:
			thrown(InvalidBooleanTermException)
	}
	
	def "AND operator throws exception if right term returns non-boolean value"() {
		when:
			use(TermOperators){
				(newSuccessValidationTerm() & newConversionTerm()).apply('')
			}
		then:
			thrown(InvalidBooleanTermException)
	}
	
	def "OR operator throws exception if left term returns non-boolean value"() {
		when:
			use(TermOperators){
				(newConversionTerm() | newValidationTerm()).apply('')
			}
		then:
			thrown(InvalidBooleanTermException)
	}
	
	def "OR operator throws exception if right term returns non-boolean value"() {
		when:
			use(TermOperators){
				(newFailValidationTerm() | newConversionTerm()).apply('')
			}
		then:
			thrown(InvalidBooleanTermException)
	}
	
	def "Validation term AND operator returns term"() {
		setup:
		  def term = newValidationTerm()
		  def andTerm
		  use(TermOperators){
  	   	andTerm = term & term
		  }
		expect:
		  andTerm instanceof Term
	}
	
	def "Validation term OR operator returns term"() {
		setup:
		  def term = newValidationTerm()
		  def orTerm
		  use(TermOperators) {
			  orTerm = term | term
		  }
		expect:
		  orTerm instanceof Term
	}
	
	def "GetAt operator for term returns subrule"() {
		setup:
		  def term = newValidationTerm()
		  def subrule
		  use(TermOperators) {
		    subrule = term[ERROR_MSG] as Subrule
		  }
		expect:
		  subrule.term == term
		  subrule.errorProperties.message == ERROR_MSG
	}

	def "AND operator for closure wraps closure"() {
		setup:
		  def closure = {}
		  def term
		  use (ClosureOperators, TermOperators) {
			  term = (closure & newValidationTerm()) as BinaryValidationTerm
		  }
		expect:
		  (term.leftTerm as ClosureTerm).closure == closure 
	}
	
	def "OR operator for closure wraps closure"() {
		setup:
		  def closure = {}
		  def term
		  use (ClosureOperators, TermOperators) {
			  term = (closure | newValidationTerm()) as BinaryValidationTerm
		  }
		expect:
		  (term.leftTerm as ClosureTerm).closure == closure
	}
	
	def "Bitiwise negation for closure returns conversion closure"() {
		setup:
		  def term
		  use (ClosureOperators) {
		    term = ~{}
		  }
		expect:
		  term instanceof TildeTerm
	}

	def "GetAt operator for closure returns subrule"() {
		setup:
		  def closure = {}
		  Subrule subrule
      use (ClosureOperators) {
        subrule = closure[ERROR_MSG] as Subrule
		  }
		expect:
		  (subrule.term as ClosureTerm).closure == closure
		  subrule.errorProperties.message == ERROR_MSG
	}

	def "Right shift for subrules sequence add new subrule"() {
		setup:
		  def subrulesSeq = newSubrulesSeq()
		  def initialSize = subrulesSeq.subrules.size()
		  use (SubrulesSeqOperators) {
  		  subrulesSeq >> newSubrule()
		  }
		expect:
	    subrulesSeq.subrules.size() == initialSize + 1
	}
}