package org.grules.ast

import static org.codehaus.groovy.syntax.Types.*
import static org.grules.TestScriptEntities.*
import static org.grules.ast.ASTTestUtils.*

import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.ast.expr.UnaryMinusExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.runtime.MethodClosure
import org.codehaus.groovy.syntax.Token
import org.grules.GroovyConstants
import org.grules.GrulesLogger
import org.grules.script.RulesScriptAPI
import org.grules.script.expressions.SubrulesSeqWrapper

import spock.lang.Specification

class RulesASTTransformationTest extends Specification {
	
	GrulesASTTransformationLogger logger
	RulesASTTransformation astTransformation
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
	def m = 'm'
	
	def setup() {
		logger = Mock()
		logger.write(_) >> {}
		astTransformation = new RulesASTTransformation()
		astTransformation.init('test')
		GrulesLogger.turnOff()
		builder = new AstBuilder()
		phase = CompilePhase.CONVERSION
		complexRuleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
			(a || b) && c >> d || e >> (f || g) >> h && i || j >> ~(k && !l)
		})
	}
		
	def "Object method calls are not included in rules"() {
		setup:
			List<BlockStatement> blockStatements = builder.buildFromCode(phase) {
				wait a
			}
		expect:
			astTransformation.visitStatements(blockStatements[0].statements).isEmpty()
	}
	
	def "Grules method calls are not included in rules"() {
		setup:
			List<BlockStatement> blockStatements = builder.buildFromCode(phase) {
				'include' a
			}
		expect:
			astTransformation.visitStatements(blockStatements[0].statements).isEmpty()
	}

	
	def "Methods called on arrays are treated as rules"() {
		setup:
			List<BlockStatement> statementBlocks = builder.buildFromCode(phase) {
				[] a
			}
		expect:
			astTransformation.visitStatements(statementBlocks[0].statements).size == 1
	}
	
	def "Methods called on GStrings are treated as rules"() {
		setup:
			List<BlockStatement> statementBlocks = builder.buildFromCode(phase) {
				"$PARAMETER_NAME" a
			}
		expect:
			astTransformation.visitStatements(statementBlocks[0].statements).size == 1
	}
	
	def "Methods called on GStrings are treated as rules (with default value)"() {
		setup:
			List<BlockStatement> statementBlocks = builder.buildFromCode(phase) {
				"$PARAMETER_NAME"[DEFAULT_VALUE] a
			}
		expect:
			astTransformation.visitStatements(statementBlocks[0].statements).size == 1
	}

	
	def "Binary expressions are converted to rules"() {
		setup:
			List<BlockStatement> statementBlocks = builder.buildFromCode(phase) {
				"$PARAMETER_NAME" {} >> a
			}
	    ExpressionStatement ruleStatement = astTransformation.visitStatements(statementBlocks[0].statements)[0]
			Expression ruleExpression = ruleStatement.expression 
		expect:
		  ruleExpression instanceof MethodCallExpression
			fetchArguments(ruleExpression).size == 2
			fetchArguments(ruleExpression)[1] instanceof ClosureExpression
	}

	def "createApplyRuleMethodCall with defaultParameters"() {
		setup:
		  def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
			  "$PARAMETER_NAME"[DEFAULT_VALUE] a
		  })
		  def methodCall = astTransformation.createRuleApplicationExpression(ruleExpression, ConstantExpression.NULL)
			def methodName = (RulesScriptAPI.&applyRuleToOptionalParameter as MethodClosure).method
		expect:
		  (methodCall.method as ConstantExpression).value == methodName 
	}
		
	def "convertToInfixExpression for expressions with one operand"() {
		setup:
			def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
				a
			})
			List infixExpression = RulesASTTransformation.convertToInfixExpression(ruleExpression)
		expect:
			checkVariable(infixExpression[0], a)
	}
	
	def "convertToInfixExpression for expressions with one operator"() {
		setup:
			def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
				a || b
			})
			List infixExpression = RulesASTTransformation.convertToInfixExpression(ruleExpression)
			int p = 0
		expect:
			checkVariable(infixExpression[p++], a)
			checkToken(infixExpression[p++], LOGICAL_OR)
			checkVariable(infixExpression[p], b)
	}
	
	def "convertToInfixExpression for not"() {
		setup:
			def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
				!a || !(b && c)
			})
			List infixExpression = RulesASTTransformation.convertToInfixExpression(ruleExpression)
			int p = 0
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
			def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
				a[b]
			})
			List infixExpression = RulesASTTransformation.convertToInfixExpression(ruleExpression)
		expect:
		  infixExpression.size == 1
			infixExpression[0] == ruleExpression 
	}
	
	def "convertToInfixExpression for expressions with parentheses"() {
		setup:
			def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
				(a || b) && c
			})
			List infixExpression = RulesASTTransformation.convertToInfixExpression(ruleExpression)
			int p = 0
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
			def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
				(a && b) || c
			})
			List infixExpression = RulesASTTransformation.convertToInfixExpression(ruleExpression)
			int p = 0
		expect:
			checkVariable(infixExpression[p++], a)
			checkToken(infixExpression[p++], LOGICAL_AND)
			checkVariable(infixExpression[p++], b)
			checkToken(infixExpression[p++], LOGICAL_OR)
			checkVariable(infixExpression[p], c)
	}
		
	def "convertToInfixExpression for complex rule expression"() {
		setup:
			def infixExpression = RulesASTTransformation.convertToInfixExpression(complexRuleExpression)
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
			def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
				~!a
			})
			def infixExpression = RulesASTTransformation.convertToInfixExpression(ruleExpression)
			def postfixExpression = RulesASTTransformation.infixToPostfixExpression(infixExpression)
			int p = 0
		expect:
		  checkVariable(postfixExpression[p++], a)
		  checkToken(postfixExpression[p++], NOT)
		  checkToken(postfixExpression[p], BITWISE_NEGATION)
	}
	
	def "infixToPostfix for complex rule expression"() {
		setup:
			def infixExpression = RulesASTTransformation.convertToInfixExpression(complexRuleExpression)
			def postfixExpression = RulesASTTransformation.infixToPostfixExpression(infixExpression)
			int p = 0
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
	  BinaryExpression binaryExpression = convertPrecedences(complexRuleExpression)
 	  assert binaryExpression.leftExpression instanceof BinaryExpression
		BinaryExpression beforeJ = binaryExpression.leftExpression
		assert beforeJ.leftExpression instanceof BinaryExpression
		BinaryExpression beforeG = beforeJ.leftExpression
		assert beforeG.leftExpression instanceof BinaryExpression
		BinaryExpression beforeE = beforeG.leftExpression
		assert beforeE.leftExpression instanceof BinaryExpression
	  BinaryExpression aOrBAndC = beforeE.leftExpression
	  assert aOrBAndC.leftExpression instanceof BinaryExpression
	  BinaryExpression aOrB = aOrBAndC.leftExpression
		checkVariable(aOrB.leftExpression, a)
		checkVariable(aOrB.rightExpression, b)
		checkVariable(aOrBAndC.rightExpression, c)
		assert beforeE.rightExpression instanceof BinaryExpression
		BinaryExpression dOrE = beforeE.rightExpression
		assert checkVariable(dOrE.leftExpression, d)
		assert checkVariable(dOrE.rightExpression, e)
  	assert beforeG.rightExpression instanceof BinaryExpression
		BinaryExpression fOrG = beforeG.rightExpression
		checkVariable(fOrG.leftExpression, f)
		checkVariable(fOrG.leftExpression, g)
		assert beforeJ.rightExpression instanceof BinaryExpression
		BinaryExpression hAndIOrJ = beforeJ.rightExpression
		assert hAndIOrJ.leftExpression instanceof BinaryExpression
		BinaryExpression hAndI = hAndIOrJ.leftExpression
		checkVariable(hAndI.leftExpression, h)
		checkVariable(hAndI.leftExpression, i)
		checkVariable(hAndIOrJ.rightExpression, j)
		assert binaryExpression.rightExpression instanceof BitwiseNegationExpression
		BitwiseNegationExpression conversionkAndNotL = binaryExpression.rightExpression
		assert conversionkAndNotL.expression instanceof BinaryExpression
		BinaryExpression kAndNotL = conversionkAndNotL.expression
		checkVariable(kAndNotL.leftExpression, k)
		assert kAndNotL.rightExpression instanceof NotExpression
		checkVariable((kAndNotL.rightExpression as NotExpression).expression, l)
	}
	
	def "convertToRuleOperations for conjunction and disjunction"() {
		setup:
  		def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
	  		a || b && c >> d
 		  })
		  ruleExpression = convertPrecedences(ruleExpression)
		  BinaryExpression binaryRuleExpression = RulesASTTransformation.convertToRuleOperators(ruleExpression)
		  BinaryExpression aOrBAndC = binaryRuleExpression.leftExpression
		  BinaryExpression bAndC = aOrBAndC.rightExpression
	  expect:
	    binaryRuleExpression.operation.type == RIGHT_SHIFT
		  aOrBAndC.operation.type == BITWISE_OR
		  bAndC.operation.type == BITWISE_AND
	}
	
	def "convertToRuleOperations for not expression"() {
		setup:
			def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
				!(a && b) || c [d] >> e
			})
			ruleExpression = convertPrecedences(ruleExpression)
			ruleExpression = RulesASTTransformation.liftErrors(ruleExpression)
			BinaryExpression convertedRuleExpression = RulesASTTransformation.convertToRuleOperators(ruleExpression)
			BinaryExpression aAndBOrCD = convertedRuleExpression.leftExpression
			BinaryExpression aAndBOrC = aAndBOrCD.leftExpression
		expect:
			aAndBOrC.leftExpression instanceof UnaryMinusExpression
			(aAndBOrC.leftExpression as UnaryMinusExpression).expression instanceof BinaryExpression
	}	
	
	def "wrapInClosures for method"() {
	  def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
			a(b)
		})
		ruleExpression = RulesASTTransformation.wrapInClosures(ruleExpression)
	expect:
	  ruleExpression instanceof ConstructorCallExpression
		fetchArguments(ruleExpression).size == 2
		fetchArguments(ruleExpression)[0] instanceof ClosureExpression
		fetchClosureExpression(fetchArguments(ruleExpression)[0]) instanceof MethodCallExpression
		fetchArguments(fetchClosureExpression(fetchArguments(ruleExpression)[0])).size == 2
	}
	
	def "wrapInClosures for bitwise negation expression"() {
		setup:
			def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
				~{a}
			})
			ruleExpression = convertPrecedences(ruleExpression)
			ruleExpression = RulesASTTransformation.liftErrors(ruleExpression)
			ruleExpression = RulesASTTransformation.wrapInClosures(ruleExpression)
		expect:
			ruleExpression instanceof BitwiseNegationExpression
			(ruleExpression as BitwiseNegationExpression).expression instanceof ClosureExpression
	}
	
	def "addSequenceWrapper for MethodCallExpression"() {
		setup:
			MethodCallExpression functionCallExpression = Mock()
			def ruleExpression = RulesASTTransformation.addSequenceWrapper(functionCallExpression)
			MethodCallExpression wrapperCallExpression = ruleExpression
			def wrapperMethodName = (SubrulesSeqWrapper.&wrap as MethodClosure).method  
		expect:
			wrapperCallExpression.objectExpression instanceof ClassExpression 
			wrapperCallExpression.method instanceof ConstantExpression
			(wrapperCallExpression.method as ConstantExpression).value == wrapperMethodName 
			fetchArguments(wrapperCallExpression).size == 1
			fetchArguments(wrapperCallExpression)[0] == functionCallExpression
	}
	
	def "addSequenceWrapper for array item expression"() {
		setup:
			BinaryExpression arrayItemExpression = Mock()
			Token token = Mock()
			token.type >> LEFT_SQUARE_BRACKET
			arrayItemExpression.operation >> token   
			def ruleExpression = RulesASTTransformation.addSequenceWrapper(arrayItemExpression)
			MethodCallExpression wrapperCallExpression = ruleExpression
		expect:
			fetchArguments(wrapperCallExpression)[0] == arrayItemExpression
	}

	def "addSequenceWrapper for right shift expression"() {
		setup:
			def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
				a >> {b}
			})
			ruleExpression = convertPrecedences(ruleExpression)
			ruleExpression = RulesASTTransformation.liftErrors(ruleExpression)
			ruleExpression = RulesASTTransformation.wrapInClosures(ruleExpression)
			ruleExpression = RulesASTTransformation.addSequenceWrapper(ruleExpression)
			assert ruleExpression instanceof BinaryExpression
			BinaryExpression aRightShiftB = ruleExpression
			def a = aRightShiftB.leftExpression
			assert a instanceof MethodCallExpression
			def methodName = ((a as MethodCallExpression).method as ConstantExpression).value
		expect:
			methodName == (SubrulesSeqWrapper.&wrap as MethodClosure).method
	}
		
	def "liftErrors for atom expression"() {
		setup:
			def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
				a
			})
			ruleExpression = convertPrecedences(ruleExpression)
			ruleExpression = RulesASTTransformation.liftErrors(ruleExpression)
		expect:
		  ruleExpression instanceof VariableExpression
	}
	
	def "liftErrors"() {
		setup:
		  def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
			  a >> b [c] >> d && e [f]
		  })
			ruleExpression = convertPrecedences(ruleExpression)
		  BinaryExpression expressionWithLiftedErrors = RulesASTTransformation.liftErrors(ruleExpression)
			BinaryExpression aRightShiftBC = expressionWithLiftedErrors.leftExpression 
	  expect:
		  checkVariable(aRightShiftBC.leftExpression, a)
			aRightShiftBC.rightExpression instanceof BinaryExpression
			(aRightShiftBC.rightExpression as BinaryExpression).operation.type == LEFT_SQUARE_BRACKET
			(expressionWithLiftedErrors.rightExpression as BinaryExpression).operation.type == LEFT_SQUARE_BRACKET
			(expressionWithLiftedErrors.rightExpression as BinaryExpression).leftExpression instanceof BinaryExpression
	}
	
	def "Labels are converted to change group methods"() {
		setup:
			List<BlockStatement> statementBlocks = builder.buildFromCode(phase) {
				POST: "$PARAMETER_NAME" {}
			}
			List<Statement> statements = statementBlocks[0].statements
			astTransformation.visitLabel(statements, statements[0])
		expect:
			statements.size > 1
	}
	
	def "createRuleExpression"() {
		def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
			"$PARAMETER_NAME" a && b [c] >> d
		})
		ruleExpression = fetchStatementBlocksExpression(ruleExpression)
		MethodCallExpression ruleApplicationExpression = astTransformation.convertToRuleExpression(expression)
		ClosureExpression closureExpression = fetchArguments(ruleApplicationExpression)[1]
		BinaryExpression ruleBinaryExpression = fetchClosureExpression(closureExpression)
		assert ruleBinaryExpression.leftExpression instanceof MethodCallExpression
		MethodCallExpression wrapperMethodCall = ruleBinaryExpression.leftExpression
		def wrapperMethodName = (SubrulesSeqWrapper.&wrap as MethodClosure).method
		assert (wrapperMethodCall.method as ConstantExpression).value == wrapperMethodName
		assert fetchArguments(ruleBinaryExpression.leftExpression).size == 1
		BinaryExpression aBC = fetchArguments(ruleBinaryExpression.leftExpression)[0]
		BinaryExpression ab = aBC.leftExpression
		assert aBC.operation.type == LEFT_SQUARE_BRACKET
		assert ab.operation.type == BITWISE_AND
		assert ab.leftExpression instanceof ConstructorCallExpression
		assert fetchArguments(ab.leftExpression)[0] instanceof ClosureExpression
		assert fetchClosureExpression(fetchArguments(ab.leftExpression)[0]) instanceof MethodCallExpression
		VariableExpression itVariable = fetchArguments(fetchClosureExpression(fetchArguments(ab.leftExpression)[0]))[0]
		assert itVariable.name == GroovyConstants.IT_NAME
		assert ab.rightExpression instanceof ConstructorCallExpression
		assert aBC.rightExpression instanceof VariableExpression
		assert ruleBinaryExpression.rightExpression instanceof ConstructorCallExpression
	}
}