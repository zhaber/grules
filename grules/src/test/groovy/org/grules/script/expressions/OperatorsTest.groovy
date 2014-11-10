package org.grules.script.expressions

import static org.grules.TestRuleEntriesFactory.createValidationTerm
import static org.grules.TestRuleEntriesFactory.createTrimConverter
import static org.grules.TestRuleEntriesFactory.createFailValidationTerm
import static org.grules.TestRuleEntriesFactory.createTildeTerm
import static org.grules.TestRuleEntriesFactory.createTerm
import static org.grules.TestRuleEntriesFactory.createConversionTerm
import static org.grules.TestRuleEntriesFactory.createSuccessValidationTerm
import static org.grules.TestRuleEntriesFactory.createSubrulesSeq
import static org.grules.TestRuleEntriesFactory.createSubrule
import static org.grules.TestScriptEntities.ERROR_ID

import org.grules.script.expressions.Operators.ClosureOperators
import org.grules.script.expressions.Operators.SubrulesSeqOperators
import org.grules.script.expressions.Operators.TermOperators
import org.grules.script.expressions.Operators.TildeTermOperators

import spock.lang.Specification

class OperatorsTest extends Specification {

	def "AND operator throws exception for unknown term"() {
		when:
		  use(TermOperators) {
		    (createValidationTerm() & createTrimConverter()).apply('')
	    }
		then:
		  thrown(InvalidBooleanTermException)
	}

	def "OR operator throws exception for unknown term"() {
		when:
		  use(TermOperators) {
		    (createFailValidationTerm() | createTrimConverter()).apply('')
	    }
		then:
		  thrown(InvalidBooleanTermException)
	}

	def "AND operator throws exception if left term is tilde term"() {
		when:
		  use(TildeTermOperators) {
  		  createTildeTerm() & createTerm()
	    }
		then:
		  thrown(InvalidBooleanTermException)
	}

	def "AND operator throws exception if right term is tilde term"() {
		when:
			use(TermOperators) {
			  createValidationTerm() & createTildeTerm()
			}
		then:
			thrown(InvalidBooleanTermException)
	}

	def "OR operator throws exception if left term is tilde term"() {
		when:
		  use(TildeTermOperators) {
  		  createTildeTerm() | createTerm()
	    }
	 	then:
		  thrown(InvalidBooleanTermException)
	}

	def "OR operator throws exception if right term is a tilde term"() {
		when:
			use(TermOperators) {
				createValidationTerm() | createTildeTerm()
			}
		then:
			thrown(InvalidBooleanTermException)
	}

	def "AND operator throws exception if left term returns non-boolean value"() {
		when:
			use(TermOperators) {
				(createConversionTerm() & createValidationTerm()).apply('')
			}
		then:
			thrown(InvalidBooleanTermException)
	}

	def "AND operator throws exception if right term returns non-boolean value"() {
		when:
			use(TermOperators) {
				(createSuccessValidationTerm() & createConversionTerm()).apply('')
			}
		then:
			thrown(InvalidBooleanTermException)
	}

	def "OR operator throws exception if left term returns non-boolean value"() {
		when:
			use(TermOperators) {
				(createConversionTerm() | createValidationTerm()).apply('')
			}
		then:
			thrown(InvalidBooleanTermException)
	}

	def "OR operator throws exception if right term returns non-boolean value"() {
		when:
			use(TermOperators) {
				(createFailValidationTerm() | createConversionTerm()).apply('')
			}
		then:
			thrown(InvalidBooleanTermException)
	}

	def "Validation term AND operator returns term"() {
		setup:
		  def term = createValidationTerm()
		  def andTerm
		  use(TermOperators) {
  	   	andTerm = term & term
		  }
		expect:
		  andTerm instanceof Term
	}

	def "Validation term OR operator returns term"() {
		setup:
		  def term = createValidationTerm()
		  def orTerm
		  use(TermOperators) {
			  orTerm = term | term
		  }
		expect:
		  orTerm instanceof Term
	}

	def "GetAt for object"() {
		setup:
		  def term = createValidationTerm()
		  def subrule
      def errorId = new Object()
		  use(TermOperators) {
		    subrule = term[errorId] as Subrule
		  }
		expect:
		  subrule.term == term
		  subrule.errorProperties.errorId == errorId
	}

  def "GetAt for string"() {
    setup:
      def term = createValidationTerm()
      def subrule
      use(TermOperators) {
        subrule = term[ERROR_ID] as Subrule
      }
    expect:
      subrule.term == term
      subrule.errorProperties.errorId == ERROR_ID
  }

	def "AND operator for closure wraps closure"() {
		setup:
		  def closure = { }
		  def term
		  use (ClosureOperators, TermOperators) {
			  term = (closure & createValidationTerm()) as BinaryValidationTerm
		  }
		expect:
		  (term.leftTerm as ClosureTerm).closure == closure
	}

	def "OR operator for closure wraps closure"() {
		setup:
		  def closure = { }
		  def term
		  use (ClosureOperators, TermOperators) {
			  term = (closure | createValidationTerm()) as BinaryValidationTerm
		  }
		expect:
		  (term.leftTerm as ClosureTerm).closure == closure
	}

	def "Bitiwise negation for closure returns conversion closure"() {
		setup:
		  def term
		  use (ClosureOperators) {
		    term = ~ { }
		  }
		expect:
		  term instanceof TildeTerm
	}

	def "GetAt operator for closure returns subrule"() {
		setup:
		  def closure = { }
		  Subrule subrule
      use (ClosureOperators) {
        subrule = closure[ERROR_ID] as Subrule
		  }
		expect:
		  (subrule.term as ClosureTerm).closure == closure
		  subrule.errorProperties.errorId == ERROR_ID
	}

	def "Right shift for subrules sequence add new subrule"() {
		setup:
		  def subrulesSeq = createSubrulesSeq()
		  def initialSize = subrulesSeq.subrules.size()
		  use (SubrulesSeqOperators) {
  		  subrulesSeq >> createSubrule()
		  }
		expect:
	    subrulesSeq.subrules.size() == initialSize + 1
	}
}
