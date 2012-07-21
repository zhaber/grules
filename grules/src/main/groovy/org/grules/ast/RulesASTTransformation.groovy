package org.grules.ast

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.ast.expr.UnaryMinusExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.runtime.MethodClosure
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.grules.functions.lib.CommonFunctions
import org.grules.functions.lib.DateFunctions
import org.grules.functions.lib.StringFunctions
import org.grules.functions.lib.TypeFunctions
import org.grules.functions.lib.UserFunctions
import org.grules.script.RulesScriptAPI
import org.grules.script.expressions.SubrulesSeqWrapper

/**
 * Transformations of an abstract syntax tree for rules scripts.
 *
 * The main transformations:
 * - wrapping first term of the first subrule into subrules sequence<br>
 * - substitution of labels with the method call that declares a corresponding group<br>
 * - substitution of logic operators with binary operators<br>
 * - lifting error expressions to a subrule level<br>
 * - wrapping functions calls into closures <br>
 * - transform "~" operator to a conversion closure
 */
@GroovyASTTransformation(phase = CompilePhase.CONVERSION)
class RulesASTTransformation extends GrulesASTTransformation {

	private static final List<Class> IMPORT_CLASSES = [CommonFunctions, 
		DateFunctions, StringFunctions, TypeFunctions, UserFunctions]
	private static final String RULES_SUFFIX = 'Grules'
	
	/** 
	 * Visits a rules script class and applies appropriate transformations.
	 */
	@Override
	void visit(ASTNode[] nodes, SourceUnit source) {
		ModuleNode moduleNode = source.ast
		if (!moduleNode || !moduleNode.classes) {
			return
		}
		String scriptName = (moduleNode.classes[0].name.split(/\\./) as List).last()
		if (scriptName.endsWith(RULES_SUFFIX)) {
			IMPORT_CLASSES.each {
				Class importClass ->
				moduleNode.addStaticStarImport(importClass.name, ClassHelper.make(importClass))
			}
			ClassNode classNode = moduleNode.classes[0]
			init(classNode.name)
	   	visit(moduleNode, classNode)
		}
	}
	
	@Override
	void visitModule(ModuleNode moduleNode, node) {
		List<Statement> statements = moduleNode.statementBlock.statements
		List<ExpressionStatement> rules = visitStatements(statements)
		rules.each { ExpressionStatement ruleExpressionStatement ->
			visitLabel(statements, ruleExpressionStatement)
		}
		log('Source code:')
	}
	
	private List<ExpressionStatement> visitStatements(List<Statement> statements) {
		statements.findAll {Statement statement ->
			if (!(statement instanceof ExpressionStatement)) {
				return false
			}
			ExpressionStatement expressionStatement = statement
			Expression ruleApplicationExpression = expressionStatement.expression
			switch (ruleApplicationExpression) {
				case BinaryExpression:
					BinaryExpression binaryExpression = ruleApplicationExpression
					if (!RuleExpressionVerifier.isFirstExpressionMethodCall(binaryExpression)) {
						return false
					}
					BinaryExpression firstMethodCallBinaryExpression = fetchFirstMethodCallBinaryExpression(binaryExpression)
					ruleApplicationExpression = firstMethodCallBinaryExpression.leftExpression
					ArgumentListExpression argumentListExpression = (ruleApplicationExpression as MethodCallExpression).arguments
					firstMethodCallBinaryExpression.leftExpression = argumentListExpression.expressions[0]
					argumentListExpression.expressions[0] = binaryExpression
					break
				case MethodCallExpression:
					break
				default:
					return false
			}
			if (!RuleExpressionVerifier.isValidRuleMethodCallExpression(ruleApplicationExpression)) {
				return false
			}
			expressionStatement.expression = convertToRuleExpression(ruleApplicationExpression)
			true
		}
	}
	
	/**
	 * Parses rule expression AST tree and makes precedence of right shift operator lower than conjunction and
	 * disjunction.
	 *
	 * @param infixExpression subrules sequence in infix form
	 * @return converted rule expression with correct operator precedence
	 */
	private Expression convertToRuleExpression(MethodCallExpression methodCallExpression) {
		Expression ruleExpression = (methodCallExpression.arguments as ArgumentListExpression).expressions[0]
		log('Original rule', ruleExpression)
		ruleExpression = RuleExpressionFormTransformer.convertPrecedences(ruleExpression)
		log('Rule with changed precedences of &, |, and >> operators', ruleExpression)
		ruleExpression = liftErrors(ruleExpression)
		ruleExpression = convertToRuleOperators(ruleExpression)
		ruleExpression = ClosureWrapper.wrapInClosures(ruleExpression)
		ruleExpression = addSequenceWrapper(ruleExpression)
		Expression ruleApplicationExpression = createRuleApplicationExpression(methodCallExpression, ruleExpression)
		log('Rule application expression', ruleApplicationExpression)
		ruleApplicationExpression
	}	
				
	private static Expression liftErrors(Expression expression) {
		if (RuleExpressionVerifier.isAtomExpression(expression)) {
			expression
		} else {
			if (GrulesASTUtils.isRightShift(expression)) {
				BinaryExpression binaryExpression = expression
				Expression leftSubrule = liftErrors(binaryExpression.leftExpression)
				Expression rightSubrule = liftErrors(binaryExpression.rightExpression)
				new BinaryExpression(leftSubrule, binaryExpression.operation, rightSubrule)
			} else {
			  liftError(expression)
			} 
		}
	}
	
	private static Expression liftError(Expression expression) {
		if (hasError(expression)) {
		  Expression expressionWithoutError = removeError(expression)
		  Integer lineNumber = expression.lineNumber
		  Integer columnNumber = expression.columnNumber
		  String arrayItemText = Types.getText(Types.LEFT_SQUARE_BRACKET)
		  Token leftSquareBracket = new Token(Types.LEFT_SQUARE_BRACKET, arrayItemText, lineNumber, columnNumber)
		  new BinaryExpression(expressionWithoutError, leftSquareBracket, fetchError(expression))
	  } else {
		  expression
	  }
	}
	
	private static boolean hasError(Expression expression) {
		if (GrulesASTUtils.isArrayItemExpression(expression)) {
			true
		} else if (expression instanceof BinaryExpression) {
			hasError((expression as BinaryExpression).rightExpression)
		} else if (expression instanceof NotExpression) {
		  hasError((expression as NotExpression).expression)
		} else if (expression instanceof BitwiseNegationExpression) {
		  hasError((expression as BitwiseNegationExpression).expression)
		} else { 
			false
		}
	}
	
	private static Expression fetchError(Expression expression) {
		if (GrulesASTUtils.isArrayItemExpression(expression)) {
			(expression as BinaryExpression).rightExpression
		} else if (expression instanceof BinaryExpression) {
			fetchError((expression as BinaryExpression).rightExpression)
		} else if (expression instanceof NotExpression){
		  fetchError((expression as NotExpression).expression)
		} else if (expression instanceof BitwiseNegationExpression){
		  fetchError((expression as BitwiseNegationExpression).expression)
		} else {
		  throw new IllegalStateException('No error found for expression ' + expression)
		}
	}
	
	private static Expression removeError(Expression expression) {
	  if (GrulesASTUtils.isArrayItemExpression(expression)) {
			(expression as BinaryExpression).leftExpression
		} else if (expression instanceof BinaryExpression) {
		  BinaryExpression binaryExpression = expression
			Expression rightExpression = removeError(binaryExpression.rightExpression)
			new BinaryExpression(binaryExpression.leftExpression, binaryExpression.operation, rightExpression) 
		} else if (expression instanceof NotExpression) {
		  Expression innerExpression = removeError((expression as NotExpression).expression)
		  new NotExpression(innerExpression)
	  } else if (expression instanceof BitwiseNegationExpression) {
		  Expression innerExpression = removeError((expression as BitwiseNegationExpression).expression)
		  new BitwiseNegationExpression(innerExpression)
	  } else {
		  expression
		}
	}

	/**
	 * Converts <code>||</code> to <code>|</code>, <code>&&</code> to <code>&</code>, <code>!</code> to <code>-</code>
	 * 
	 * @param expression rule expression
	 * @return rule expression with converted operators
	 */
	private static Expression convertToRuleOperators(Expression expression) {
		if (RuleExpressionVerifier.isAtomExpression(expression)) {
			expression
		} else if (expression instanceof NotExpression) {
		  Expression innerExpression = convertToRuleOperators((expression as NotExpression).expression)
			new UnaryMinusExpression(innerExpression)
	  } else if (expression instanceof BitwiseNegationExpression) {
		  Expression innerExpression = convertToRuleOperators((expression as BitwiseNegationExpression).expression)
			new BitwiseNegationExpression(innerExpression)
	  } else if (GrulesASTUtils.isArrayItemExpression(expression)) {
		  BinaryExpression binaryExpression = expression
		  Expression leftExpression = convertToRuleOperators(binaryExpression.leftExpression)
		  new BinaryExpression(leftExpression, binaryExpression.operation, binaryExpression.rightExpression)
	  } else if (expression instanceof BinaryExpression) {
		  BinaryExpression binaryExpression = expression
		  Expression leftExpression = convertToRuleOperators(binaryExpression.leftExpression)
		  Expression rightExpression = convertToRuleOperators(binaryExpression.rightExpression)
		  Token operation = binaryExpression.operation
	  	operation.type = convertToBitwiseOperation(operation.type)
  		new BinaryExpression(leftExpression, operation, rightExpression)
		} else {
		  throw new IllegalStateException(expression.class)
		}
	}
	
	private static Integer convertToBitwiseOperation(Integer type) {
		switch (type) {
			case Types.LOGICAL_OR: return Types.BITWISE_OR
			case Types.LOGICAL_AND: return Types.BITWISE_AND
			default: type
		}
	}	
	
	private static Expression createRuleApplicationExpression(MethodCallExpression methodCallExpression,
	    Expression rule) {
		Expression objectExpression = methodCallExpression.objectExpression
		Expression ruleClosureExpression = GrulesASTFactory.createClosureExpression(rule)
		if (objectExpression == VariableExpression.THIS_EXPRESSION) {
			Expression parameterExpression = methodCallExpression.method
			List<Expression> arguments = [parameterExpression, ruleClosureExpression]
			GrulesASTFactory.createMethodCall(RulesScriptAPI.&applyRuleToRequiredParameter, arguments)
		} else if (objectExpression instanceof ListExpression) {
			List<Expression> arguments = [objectExpression, ruleClosureExpression]
			GrulesASTFactory.createMethodCall(RulesScriptAPI.&applyRuleToParametersGroup, arguments)
		} else if (objectExpression instanceof BinaryExpression) {
		  BinaryExpression binaryExpression = objectExpression
			List<Expression> arguments = []
			if (binaryExpression.leftExpression instanceof VariableExpression) {
				arguments << new ConstantExpression((binaryExpression.leftExpression as VariableExpression).name)
			} else {
				arguments << binaryExpression.leftExpression
			}
			Expression defaultValue = binaryExpression.rightExpression
			arguments += [ruleClosureExpression, defaultValue]
			GrulesASTFactory.createMethodCall(RulesScriptAPI.&applyRuleToOptionalParameter, arguments)
		} else {
		  throw new IllegalStateException(objectExpression.class)
		}
	}

	private void visitLabel(List<Statement> statements, ExpressionStatement statement) {
		if (statement.statementLabel != null) {
			Integer groupLabelStatementIndex = statements.indexOf(statement)
			log('Creating method call for group', statement.statementLabel)
			MethodCallExpression changeGroupMethodCall = GrulesASTFactory.createMethodCall(
			    RulesScriptAPI.&changeGroup, [new ConstantExpression(statement.statementLabel)])
			statements.add(groupLabelStatementIndex, new ExpressionStatement(changeGroupMethodCall))
			log('Added method call', (RulesScriptAPI.&changeGroup as MethodClosure).method + 
				    "($statement.statementLabel)")
		}
	}
	
	private static BinaryExpression fetchFirstMethodCallBinaryExpression(BinaryExpression binaryExpression) {
	  if (binaryExpression.leftExpression instanceof MethodCallExpression) {
			binaryExpression
 	  } else { 
	    fetchFirstMethodCallBinaryExpression(binaryExpression.leftExpression)
 	  }
	}
	
	private static Expression addSequenceWrapper(Expression expression) {
		if (GrulesASTUtils.isRightShift(expression)) {
			BinaryExpression binaryExpression = expression
			Expression leftExpression = addSequenceWrapper(binaryExpression.leftExpression)
			new BinaryExpression(leftExpression, binaryExpression.operation, binaryExpression.rightExpression)
		} else {
			GrulesASTFactory.createStaticMethodCall(SubrulesSeqWrapper, SubrulesSeqWrapper.&wrap, [expression])
		} 
	}
}