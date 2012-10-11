package org.grules.ast

import static org.codehaus.groovy.syntax.Types.*
import static org.grules.TestScriptEntities.*
import static org.grules.ast.ASTTestUtils.*

import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.control.CompilePhase
import org.grules.GrulesLogger

import spock.lang.Specification

class RuleExpressionFormTransformerTest extends Specification {

	GrulesASTTransformationLogger logger
	RulesAstTransformation astTransformation
	AstBuilder builder
	CompilePhase phase
	Expression complexRuleExpression

	def a = 'a'
	def b = 'b'
	def c = 'c'
	def d = 'd'
	def e = 'e'
	def f = 'f'
	def g = 'g'
	def h = 'h'
	def i = 'i'
	def j = 'j'
	def k = 'k'
	def l = 'l'

	def setup() {
		logger = Mock()
		logger.write(_) >> {}
		astTransformation = new RulesAstTransformation()
		astTransformation.init('test')
		GrulesLogger.turnOff()
		builder = new AstBuilder()
		phase = CompilePhase.CONVERSION
		complexRuleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
			(a || b) && c >> d || e >> (f || g) >> h && i || j >> ~(k && !l)
		})
	}

	def "convertToInfixExpression for expressions with one operand"() {
		setup:
			def ruleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
				a
			})
			List infixExpression = RuleExpressionFormTransformer.transformToInfixExpression(ruleExpression)
		expect:
			checkVariable(infixExpression[0], a)
	}

	def "convertToInfixExpression for expressions with one operator"() {
		setup:
			def ruleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
				a || b
			})
			List infixExpression = RuleExpressionFormTransformer.transformToInfixExpression(ruleExpression)
			def p = 0
		expect:
			checkVariable(infixExpression[p++], a)
			checkToken(infixExpression[p++], LOGICAL_OR)
			checkVariable(infixExpression[p], b)
	}

	def "convertToInfixExpression for not"() {
		setup:
			def ruleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
				!a || !(b && c)
			})
			List infixExpression = RuleExpressionFormTransformer.transformToInfixExpression(ruleExpression)
			def p = 0
		expect:
			checkToken(infixExpression[p++], NOT)
			checkVariable(infixExpression[p++], a)
			checkToken(infixExpression[p++], LOGICAL_OR)
			checkToken(infixExpression[p++], NOT)
			checkToken(infixExpression[p++], LEFT_PARENTHESIS)
			checkVariable(infixExpression[p++], b)
			checkToken(infixExpression[p++], LOGICAL_AND)
			checkVariable(infixExpression[p++], c)
			checkToken(infixExpression[p], RIGHT_PARENTHESIS)
	}

	def "convertToInfixExpression for expression with error"() {
		setup:
			def ruleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
				a[b]
			})
			List infixExpression = RuleExpressionFormTransformer.transformToInfixExpression(ruleExpression)
		expect:
			infixExpression.size == 1
			infixExpression[0] == ruleExpression
	}

	def "convertToInfixExpression for expressions with parentheses"() {
		setup:
			def ruleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
				(a || b) && c
			})
			List infixExpression = RuleExpressionFormTransformer.transformToInfixExpression(ruleExpression)
			def p = 0
		expect:
			checkToken(infixExpression[p++], LEFT_PARENTHESIS)
			checkVariable(infixExpression[p++], a)
			checkToken(infixExpression[p++],LOGICAL_OR)
			checkVariable(infixExpression[p++], b)
			checkToken(infixExpression[p++],RIGHT_PARENTHESIS)
			checkToken(infixExpression[p++],LOGICAL_AND)
			checkVariable(infixExpression[p], c)
	}

	def "convertToInfixExpression for expressions with redundant parentheses"() {
		setup:
			def ruleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
				(a && b) || c
			})
			List infixExpression = RuleExpressionFormTransformer.transformToInfixExpression(ruleExpression)
			def p = 0
		expect:
			checkVariable(infixExpression[p++], a)
			checkToken(infixExpression[p++], LOGICAL_AND)
			checkVariable(infixExpression[p++], b)
			checkToken(infixExpression[p++], LOGICAL_OR)
			checkVariable(infixExpression[p], c)
	}

	def "convertToInfixExpression for complex rule expression"() {
		setup:
			def infixExpression = RuleExpressionFormTransformer.transformToInfixExpression(complexRuleExpression)
			def p = 0
		expect:
			checkToken(infixExpression[p++], LEFT_PARENTHESIS)
			checkVariable(infixExpression[p++], a)
			checkToken(infixExpression[p++], LOGICAL_OR)
			checkVariable(infixExpression[p++], b)
			checkToken(infixExpression[p++], RIGHT_PARENTHESIS)
			checkToken(infixExpression[p++], LOGICAL_AND)
			checkVariable(infixExpression[p++], c)
			checkToken(infixExpression[p++], RIGHT_SHIFT)
			checkVariable(infixExpression[p++], d)
			checkToken(infixExpression[p++], LOGICAL_OR)
			checkVariable(infixExpression[p++], e)
			checkToken(infixExpression[p++], RIGHT_SHIFT)
			checkToken(infixExpression[p++], LEFT_PARENTHESIS)
			checkVariable(infixExpression[p++], f)
			checkToken(infixExpression[p++], LOGICAL_OR)
			checkVariable(infixExpression[p++], g)
			checkToken(infixExpression[p++], RIGHT_PARENTHESIS)
			checkToken(infixExpression[p++], RIGHT_SHIFT)
			checkVariable(infixExpression[p++], h)
			checkToken(infixExpression[p++], LOGICAL_AND)
			checkVariable(infixExpression[p++], i)
			checkToken(infixExpression[p++], LOGICAL_OR)
			checkVariable(infixExpression[p++], j)
			checkToken(infixExpression[p++], RIGHT_SHIFT)
			checkToken(infixExpression[p++], BITWISE_NEGATION)
			checkToken(infixExpression[p++], LEFT_PARENTHESIS)
			checkVariable(infixExpression[p++], k)
			checkToken(infixExpression[p++], LOGICAL_AND)
			checkToken(infixExpression[p++], NOT)
			checkVariable(infixExpression[p++], l)
			checkToken(infixExpression[p++], RIGHT_PARENTHESIS)
	}

	def "infixToPostfixExpression for not and bitwise expression"() {
		setup:
			def ruleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
				~!a
			})
			def infixExpression = RuleExpressionFormTransformer.transformToInfixExpression(ruleExpression)
			def postfixExpression = RuleExpressionFormTransformer.infixToPostfixExpression(infixExpression)
			def p = 0
		expect:
			checkVariable(postfixExpression[p++], a)
			checkToken(postfixExpression[p++], NOT)
			checkToken(postfixExpression[p], BITWISE_NEGATION)
	}

	def "infixToPostfix for complex rule expression"() {
		setup:
			def infixExpression = RuleExpressionFormTransformer.transformToInfixExpression(complexRuleExpression)
			def postfixExpression = RuleExpressionFormTransformer.infixToPostfixExpression(infixExpression)
			def p = 0
		expect:
			checkVariable(postfixExpression[p++], a)
			checkVariable(postfixExpression[p++], b)
			checkToken(postfixExpression[p++], LOGICAL_OR)
			checkVariable(postfixExpression[p++], c)
			checkToken(postfixExpression[p++], LOGICAL_AND)
			checkVariable(postfixExpression[p++], d)
			checkVariable(postfixExpression[p++], e)
			checkToken(postfixExpression[p++], LOGICAL_OR)
			checkToken(postfixExpression[p++], RIGHT_SHIFT)
			checkVariable(postfixExpression[p++], f)
			checkVariable(postfixExpression[p++], g)
			checkToken(postfixExpression[p++], LOGICAL_OR)
			checkToken(postfixExpression[p++], RIGHT_SHIFT)
			checkVariable(postfixExpression[p++], h)
			checkVariable(postfixExpression[p++], i)
			checkToken(postfixExpression[p++], LOGICAL_AND)
			checkVariable(postfixExpression[p++], j)
			checkToken(postfixExpression[p++], LOGICAL_OR)
			checkToken(postfixExpression[p++], RIGHT_SHIFT)
			checkVariable(postfixExpression[p++], k)
			checkVariable(postfixExpression[p++], l)
			checkToken(postfixExpression[p++], NOT)
			checkToken(postfixExpression[p++], LOGICAL_AND)
			checkToken(postfixExpression[p++], BITWISE_NEGATION)
			checkToken(postfixExpression[p], RIGHT_SHIFT)
	}

	def "postfixExpressionToTree for complex rule expression"() {
    when:
		  BinaryExpression binaryExpression = RuleExpressionFormTransformer.convertPrecedences(complexRuleExpression)
    then:
		  binaryExpression.leftExpression instanceof BinaryExpression
    when:
		  BinaryExpression beforeJ = binaryExpression.leftExpression
		then:
      beforeJ.leftExpression instanceof BinaryExpression
    when:
		  BinaryExpression beforeG = beforeJ.leftExpression
		then:
      beforeG.leftExpression instanceof BinaryExpression
    when:
		  BinaryExpression beforeE = beforeG.leftExpression
		then:
      beforeE.leftExpression instanceof BinaryExpression
    when:
		  BinaryExpression aOrBAndC = beforeE.leftExpression
		then:
      aOrBAndC.leftExpression instanceof BinaryExpression
		when:
      BinaryExpression aOrB = aOrBAndC.leftExpression
    then:
		  checkVariable(aOrB.leftExpression, a)
		  checkVariable(aOrB.rightExpression, b)
		  checkVariable(aOrBAndC.rightExpression, c)
		  beforeE.rightExpression instanceof BinaryExpression
    when:
		  BinaryExpression dOrE = beforeE.rightExpression
    then:
		  checkVariable(dOrE.leftExpression, d)
		  checkVariable(dOrE.rightExpression, e)
		  beforeG.rightExpression instanceof BinaryExpression
    when:
		  BinaryExpression fOrG = beforeG.rightExpression
    then:
		  checkVariable(fOrG.leftExpression, f)
		  checkVariable(fOrG.rightExpression, g)
		  beforeJ.rightExpression instanceof BinaryExpression
    when:
		  BinaryExpression hAndIOrJ = beforeJ.rightExpression
    then:
		  hAndIOrJ.leftExpression instanceof BinaryExpression
    when:
		  BinaryExpression hAndI = hAndIOrJ.leftExpression
    then:
		  checkVariable(hAndI.leftExpression, h)
		  checkVariable(hAndI.rightExpression, i)
		  checkVariable(hAndIOrJ.rightExpression, j)
	    binaryExpression.rightExpression instanceof BitwiseNegationExpression
    when:
		  BitwiseNegationExpression conversionkAndNotL = binaryExpression.rightExpression
    then:
		  conversionkAndNotL.expression instanceof BinaryExpression
    when:
		  BinaryExpression kAndNotL = conversionkAndNotL.expression
    then:
		  checkVariable(kAndNotL.leftExpression, k)
		  kAndNotL.rightExpression instanceof NotExpression
		  checkVariable((kAndNotL.rightExpression as NotExpression).expression, l)
	}

}