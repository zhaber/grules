package org.grules.ast

import static org.codehaus.groovy.syntax.Types.*
import static org.grules.TestScriptEntities.*
import static org.grules.ast.ASTTestUtils.*

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.CastExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.UnaryMinusExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.runtime.MethodClosure
import org.codehaus.groovy.syntax.Token
import org.grules.GrulesLogger
import org.grules.script.Parameter
import org.grules.script.Rule
import org.grules.script.RulesScriptAPI
import org.grules.script.expressions.SubrulesSeqWrapper

import spock.lang.Specification

class RulesASTTransformationTest extends Specification {

	GrulesASTTransformationLogger logger
	RulesAstTransformation astTransformation
	AstBuilder builder
	CompilePhase phase
	Expression complexRuleExpression
	RuleExpressionFormTransformer ruleExpressionFormTransformer = new RuleExpressionFormTransformer()

	def a = 'a'
	def b = 'b'
	def c = 'c'
	def d = 'd'
	def e = 'd'
	def f = 'f'

	def setup() {
		logger = Mock()
		logger.write(_) >> {}
		astTransformation = new RulesAstTransformation()
		astTransformation.init('test')
		GrulesLogger.turnOff()
		builder = new AstBuilder()
		phase = CompilePhase.CONVERSION
	}

  def isTransformed(List<BlockStatement> statementBlocks) {
    ExpressionStatement statement = statementBlocks[0].statements[0]
    def expression = statement.expression
    astTransformation.visitStatement(statement)
    expression != statement.expression
  }

	def "Object method calls are not included in rules"() {
		expect:
			!isTransformed(builder.buildFromCode(phase) {
				wait a
			})
	}

	def "Grules method calls are not included in rules"() {
    expect:
      !isTransformed(builder.buildFromCode(phase) {
        'include' a
      })
	}

	def "Methods called on list are treated as rules"() {
		expect:
			isTransformed(builder.buildFromCode(phase) {
				[] a
			})
	}

  def "If condition is transformed"() {
    setup:
      List<BlockStatement> statementBlocks = builder.buildFromCode(phase) {
        if (a) {
          b c
        }
      }
      IfStatement statement = statementBlocks[0].statements[0]
      BlockStatement ifBlockExpressionStatement = statement.ifBlock
      ExpressionStatement expressionStatement = ifBlockExpressionStatement.statements[0]
      def expression = expressionStatement.expression
      astTransformation.visitStatement(statement)
    expect:
      expression != expressionStatement.expression
  }

  def "Parameter annotation"() {
    setup:
    List<BlockStatement> statementBlocks = builder.buildFromCode(phase) {
      @Parameter
      a = b
      a c
    }
    ExpressionStatement statement = statementBlocks[0].statements[0]
    astTransformation.visitStatement(statement)
  expect:
    statement.expression instanceof MethodCallExpression
  }

  def "Rule annotation"() {
    setup:
    List<BlockStatement> statementBlocks = builder.buildFromCode(phase) {
      @Rule
      a = {b,c -> {->}}
      a d
    }
    ExpressionStatement statement = statementBlocks[0].statements[0]
    astTransformation.visitStatement(statement)
    ClosureExpression closureExpression = (statement.expression as DeclarationExpression).rightExpression
    Expression expression = ((closureExpression.code as BlockStatement).statements[0] as ExpressionStatement).expression
  expect:
    expression instanceof MethodCallExpression
    fetchArguments(expression).size == 1
    fetchArguments(expression)[0] instanceof VariableExpression
    (fetchArguments(expression)[0] as VariableExpression).name == b
  }

  def "Combined required and optional parameters are transformed to API method parameters"() {
    setup:
      def ruleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
        [a[b], c] d
      })
      def methodCall = astTransformation.createRuleApplicationExpression(ruleExpression, ConstantExpression.NULL)
      def methodName = (RulesScriptAPI.&applyRuleToParametersList as MethodClosure).method
      def arguments = fetchArguments(methodCall)
    expect:
      ((methodCall as MethodCallExpression).method as ConstantExpression).value == methodName
      arguments[0] instanceof ConstantExpression
      arguments[1] instanceof CastExpression
      arguments[2] instanceof MapExpression
      arguments[3] instanceof ClosureExpression
  }

	def "Methods called on GStrings are treated as rules"() {
		expect:
			isTransformed(builder.buildFromCode(phase) {
				"$PARAMETER_NAME" a
			})
	}

	def "Methods called on GStrings are treated as rules (with default value)"() {
		expect:
			isTransformed(builder.buildFromCode(phase) {
				"$PARAMETER_NAME"[DEFAULT_VALUE] a
			})
	}


	def "Binary expressions are converted to rules"() {
		setup:
			List<BlockStatement> statementBlocks = builder.buildFromCode(phase) {
				"$PARAMETER_NAME" {} >> a
			}
      ExpressionStatement statement = statementBlocks[0].statements[0]
	    astTransformation.visitStatement(statement)
			Expression ruleExpression = statement.expression
		expect:
		  ruleExpression instanceof MethodCallExpression
			fetchArguments(ruleExpression).size == 2
			fetchArguments(ruleExpression)[1] instanceof ClosureExpression
	}

	def "createApplyRuleMethodCall with defaultParameters"() {
		setup:
		  def ruleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
			  "$PARAMETER_NAME"[DEFAULT_VALUE] a
		  })
		  def methodCall = astTransformation.createRuleApplicationExpression(ruleExpression, ConstantExpression.NULL)
			def methodName = (RulesScriptAPI.&applyRuleToOptionalParameter as MethodClosure).method
		expect:
		  ((methodCall as MethodCallExpression).method as ConstantExpression).value == methodName
	}

	def "convertToRuleOperations for conjunction and disjunction"() {
		setup:
  		def ruleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
	  		a || b && c >> d
 		  })
		  ruleExpression = ruleExpressionFormTransformer.convertPrecedences(ruleExpression)
		  BinaryExpression binaryRuleExpression = RulesAstTransformation.convertToRuleOperators(ruleExpression)
		  BinaryExpression aOrBAndC = binaryRuleExpression.leftExpression
		  BinaryExpression bAndC = aOrBAndC.rightExpression
	  expect:
	    binaryRuleExpression.operation.type == RIGHT_SHIFT
		  aOrBAndC.operation.type == BITWISE_OR
		  bAndC.operation.type == BITWISE_AND
	}

	def "convertToRuleOperations for not expression"() {
		setup:
			def ruleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
				!(a && b) || c [d] >> e
			})
			ruleExpression = ruleExpressionFormTransformer.convertPrecedences(ruleExpression)
			ruleExpression = RulesAstTransformation.liftErrors(ruleExpression)
			BinaryExpression convertedRuleExpression = RulesAstTransformation.convertToRuleOperators(ruleExpression)
			BinaryExpression aAndBOrCD = convertedRuleExpression.leftExpression
			BinaryExpression aAndBOrC = aAndBOrCD.leftExpression
		expect:
			aAndBOrC.leftExpression instanceof UnaryMinusExpression
			(aAndBOrC.leftExpression as UnaryMinusExpression).expression instanceof BinaryExpression
	}

	def "addSequenceWrapper for MethodCallExpression"() {
		setup:
			MethodCallExpression functionCallExpression = Mock()
			def ruleExpression = RulesAstTransformation.addSequenceWrapper(functionCallExpression)
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
			def ruleExpression = RulesAstTransformation.addSequenceWrapper(arrayItemExpression)
			MethodCallExpression wrapperCallExpression = ruleExpression
		expect:
			fetchArguments(wrapperCallExpression)[0] == arrayItemExpression
	}

	def "addSequenceWrapper for right shift expression"() {
		setup:
			def ruleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
				a >> {b}
			})
			ruleExpression = ruleExpressionFormTransformer.convertPrecedences(ruleExpression)
			ruleExpression = RulesAstTransformation.liftErrors(ruleExpression)
			ruleExpression = ClosureWrapper.wrapInClosures(ruleExpression)
			ruleExpression = RulesAstTransformation.addSequenceWrapper(ruleExpression)
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
			def ruleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
				a
			})
			ruleExpression = ruleExpressionFormTransformer.convertPrecedences(ruleExpression)
			ruleExpression = RulesAstTransformation.liftErrors(ruleExpression)
		expect:
		  ruleExpression instanceof VariableExpression
	}

	def "liftErrors"() {
		setup:
		  def ruleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
			  a >> b [c] >> d && e [f]
		  })
			ruleExpression = ruleExpressionFormTransformer.convertPrecedences(ruleExpression)
		  BinaryExpression expressionWithLiftedErrors = RulesAstTransformation.liftErrors(ruleExpression)
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
      ModuleNode moduleNode = Mock()
      ClassNode classNode = Mock()
      moduleNode.statementBlock >> statementBlocks[0]
			astTransformation.visitModule(moduleNode, classNode)
		expect:
			statementBlocks[0].statements.size > 1
	}

	def "createRuleExpression"() {
    setup:
		  def expression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
			  "$PARAMETER_NAME" a && b [c] >> d
		  })
		  MethodCallExpression ruleApplicationExpression = astTransformation.convertToRuleApplicationExpression(expression)
		  ClosureExpression closureExpression = fetchArguments(ruleApplicationExpression)[1]
    when:
		  BinaryExpression ruleBinaryExpression = fetchClosureExpression(closureExpression)
    then:
		  ruleBinaryExpression.leftExpression instanceof MethodCallExpression
		when:
      MethodCallExpression wrapperMethodCall = ruleBinaryExpression.leftExpression
		  def wrapperMethodName = (SubrulesSeqWrapper.&wrap as MethodClosure).method
    then:
		  (wrapperMethodCall.method as ConstantExpression).value == wrapperMethodName
		  fetchArguments(ruleBinaryExpression.leftExpression).size == 1
    when:
		  BinaryExpression aBC = fetchArguments(ruleBinaryExpression.leftExpression)[0]
		  BinaryExpression ab = aBC.leftExpression
    then:
		  aBC.operation.type == LEFT_SQUARE_BRACKET
		  ab.operation.type == BITWISE_AND
		  ab.leftExpression instanceof ConstructorCallExpression
		  fetchArguments(ab.leftExpression)[0] instanceof ClosureExpression
		  fetchClosureExpression(fetchArguments(ab.leftExpression)[0]) instanceof MethodCallExpression
    when:
		  VariableExpression itVariable = fetchArguments(fetchClosureExpression(fetchArguments(ab.leftExpression)[0]))[0]
    then:
		  itVariable.name == ExpressionFactory.IT_NAME
		  ab.rightExpression instanceof ConstructorCallExpression
		  aBC.rightExpression instanceof VariableExpression
		  ruleBinaryExpression.rightExpression instanceof ConstructorCallExpression
	}
}