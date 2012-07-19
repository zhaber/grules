package org.grules.ast

import static org.codehaus.groovy.syntax.Types.*
import static org.grules.TestScriptEntities.*
import static org.grules.ast.ASTTestUtils.*

import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
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
	def e = 'd'
	def f = 'f'
	
	def setup() {
		logger = Mock()
		logger.write(_) >> {}
		astTransformation = new RulesASTTransformation()
		astTransformation.init('test')
		GrulesLogger.turnOff()
		builder = new AstBuilder()
		phase = CompilePhase.CONVERSION
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
			ruleExpression = ClosureWrapper.wrapInClosures(ruleExpression)
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