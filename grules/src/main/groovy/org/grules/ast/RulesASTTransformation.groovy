package org.grules.ast

import groovy.inspect.swingui.AstNodeToScriptVisitor

import java.lang.reflect.Method

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
import org.codehaus.groovy.ast.expr.GStringExpression
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
import org.grules.GroovyConstants
import org.grules.functions.lib.CommonFunctions
import org.grules.functions.lib.DateFunctions
import org.grules.functions.lib.StringFunctions
import org.grules.script.RulesScriptAPI
import org.grules.script.expressions.FunctionTerm
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

	private static final VariableExpression IT_VARIABLE = new VariableExpression(GroovyConstants.IT_NAME)
	private static final List<Class> IMPORT_CLASSES = [CommonFunctions, DateFunctions, StringFunctions]
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
					if (!isFirstExpressionMethodCall(binaryExpression)) {
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
			if (!isValidRuleMethodCallExpression(ruleApplicationExpression)) {
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
	private MethodCallExpression convertToRuleExpression(MethodCallExpression methodCallExpression) {
		Expression ruleExpression = (methodCallExpression.arguments as ArgumentListExpression).expressions[0]
		log('Original rule', ruleExpression)
		List infixExpression = convertToInfixExpression(ruleExpression)
		log('Rule in infix form', infixExpression)
		Deque postfixExpression = infixToPostfixExpression(infixExpression)
		log('Rule in postfix form', postfixExpression)
		ruleExpression = postfixExpressionToTree(postfixExpression)
		log('Rule with changed precedence of &, |, and >>', ruleExpression)
		ruleExpression = liftErrors(ruleExpression)
		ruleExpression = convertToRuleOperators(ruleExpression)
		ruleExpression = wrapInClosures(ruleExpression)
		ruleExpression = addSequenceWrapper(ruleExpression)
		MethodCallExpression ruleApplicationExpression = createRuleApplicationExpression(methodCallExpression, ruleExpression)
		log('Rule application expression', ruleApplicationExpression)
		ruleApplicationExpression
	}
	
	private static Integer fetchOperatorPrecedence(Token token) {
		switch (token.type) {
		  case Types.LEFT_PARENTHESIS: return 0
			case Types.RIGHT_PARENTHESIS: return 1
		  case Types.RIGHT_SHIFT: return 2
			case Types.LOGICAL_OR: return 3
			case Types.LOGICAL_AND: return  4
			case Types.BITWISE_NEGATION:
			case Types.NOT: return 5
			default: throw new UnsupportedExpressionException(token.text)
		}
	}
	
	/**
	 * Removes tokens that represent operation with lower priority than the given operation. 
	 */
	private static Deque<Token> popLowerPriorityTokens(Stack<Token> stack, Token operation) {
		Deque<Token> tokens = [] as Queue
		Token stackTop = stack.peek()
		//we use >= because a+b+c should to be transformed to ab+c+ not abc++
		while (fetchOperatorPrecedence(stackTop) >= fetchOperatorPrecedence(operation) && !stack.empty()) {
			tokens << stack.pop()
			if (!stack.empty()) {
				stackTop = stack.peek()
			}
		}
		tokens
	}
	
	/**
	 * Converts infix notation to postfix notation.
	 */
  private static List<Object> infixToPostfixExpression(List<Object> infixExpression){
	  Stack<Token> stack = [] as Stack
	  Deque<Object> postfixExpression = [] as Queue
    infixExpression.each { token ->
	    if (token instanceof Expression) {
		     postfixExpression << token
	    } else {
			  Token operation = token
		    if (stack.empty() || operation.type == Types.LEFT_PARENTHESIS) {
			    stack << operation
		    } else {
					if (!(operation.type in [Types.NOT, Types.BITWISE_NEGATION])) {
						postfixExpression += popLowerPriorityTokens(stack, operation)
					}
					if (operation.type == Types.RIGHT_PARENTHESIS) {
						// remove left the corresponding left parenthesis from the stack 
  			    stack.pop()
					} else {
						stack << operation
					}
		    }
	    }
    }
    while (!stack.isEmpty()) {
      postfixExpression << stack.pop()
	  }
 	  postfixExpression
  }

	/**
	 * Converts postfix expression to expression represented as a tree
	 */
  private static Expression postfixExpressionToTree(Deque postfixExpression) {
	  Stack<Object> stack = [] as Stack
	  while (!postfixExpression.isEmpty()) {
		  def expression = postfixExpression.removeFirst()
		  if (expression instanceof Expression) {
		 	  stack << expression
		  } else if (expression instanceof Token) {
			  Token token = expression
			  if (isValidRuleBinaryOperation(token)) {
			    Expression rightExpression = stack.pop()
			    Expression leftExpression = stack.pop()
			    stack << new BinaryExpression(leftExpression, token, rightExpression)
			  } else if (token.type == Types.NOT) {
				  stack << new NotExpression(stack.pop())
			  } else if (token.type == Types.BITWISE_NEGATION) {
				  stack << new BitwiseNegationExpression(stack.pop())
			  } else {
				  throw new UnsupportedExpressionException(token.text)
			  } 
		  } else {
			  throw new UnsupportedExpressionException(expression.class)
		  }
	  }
	  stack.pop()
  }
	
	private static boolean isAtomExpression(Expression expression) {
		if (expression instanceof BinaryExpression) {
			BinaryExpression binaryExpression = expression
			GrulesASTUtils.isArrayItemExpression(binaryExpression) && isAtomExpression(binaryExpression.leftExpression) 
		} else {
		  !(expression instanceof NotExpression) && !(expression instanceof BitwiseNegationExpression)
		} 
	}
	
	private static List convertInnerExpressionsToInfix(Expression expression, Token operation) {
		Integer operationPrecedence = GrulesASTUtils.fetchPrecedence(operation)
		if (expression instanceof BinaryExpression) {
			BinaryExpression binaryExpression = expression
			List leftExpression = convertToInfixExpression(binaryExpression.leftExpression, operationPrecedence)
			List rightExpression = convertToInfixExpression(binaryExpression.rightExpression, operationPrecedence)
			leftExpression + [operation] + rightExpression
		} else if (expression instanceof NotExpression) {
			NotExpression notExpression = expression
			List innerExpression = convertToInfixExpression(notExpression.expression, operationPrecedence)
			[operation] + innerExpression
		} else if (expression instanceof BitwiseNegationExpression) {
		  BitwiseNegationExpression bitwiseExpression = expression
		  List innerExpression = convertToInfixExpression(bitwiseExpression.expression, operationPrecedence)
		  [operation] + innerExpression
		} else {
			throw new UnsupportedExpressionException(expression.class)
		}
	}
	
	/**
	 * Traverses rule expression AST inorder and converts to infix notation. Parenthesis are added when necessary.
	 *
	 * @param expression rule expression
	 * @return rule expression in infix form
	 */
	private static List convertToInfixExpression(Expression expression, 
		  Integer maxPrecedence = Integer.MIN_VALUE) {
		if (isAtomExpression(expression)) {
			return [expression]
		}
		Token operation = GrulesASTUtils.fetchOperationToken(expression)
		List infixExpression = convertInnerExpressionsToInfix(expression, operation)
		if (GrulesASTUtils.fetchPrecedence(operation) >= maxPrecedence) {
			infixExpression
		} else {
			Integer lineNumber = expression.lineNumber
			Integer columnNumber = expression.columnNumber
			String leftParenthesisText = Types.getText(Types.LEFT_PARENTHESIS)
			String rightParenthesisText = Types.getText(Types.RIGHT_PARENTHESIS)
			Token leftParenthesis = new Token(Types.LEFT_PARENTHESIS, leftParenthesisText, lineNumber, columnNumber)
			Token rightParenthesis = new Token(Types.RIGHT_PARENTHESIS, rightParenthesisText, lineNumber, columnNumber)
			[leftParenthesis] + infixExpression + [rightParenthesis]
		}
	}
			
	/**
	 * Add an artificial first argument to shortcut methods.
	 *  
	 * @param expression without value place holders
	 * @return expression with added artificial arguments
	 */
	private static Expression wrapInClosures(Expression expression) {
		if (expression instanceof MethodCallExpression) {
			MethodCallExpression methodCallExpression = expression
			List<Expression> arguments = (methodCallExpression.arguments as ArgumentListExpression).expressions
			arguments = [IT_VARIABLE] + arguments 
			Expression method = methodCallExpression.method
			MethodCallExpression closureMethodCallExpression = GrulesASTFactory.createMethodCall(method, arguments)
			Expression closure = GrulesASTFactory.createSingleExpressionClosure(closureMethodCallExpression)
			Writer stringWriter = new StringWriter()
			AstNodeToScriptVisitor stringAstNodeToScriptVisitor = new AstNodeToScriptVisitor(stringWriter)
			method.visit(stringAstNodeToScriptVisitor)
			GrulesASTFactory.createConstructorCall(FunctionTerm, [closure, new ConstantExpression(stringWriter.toString())])
		} else if (expression instanceof VariableExpression) {
			String variableName = (expression as VariableExpression).name 
			MethodCallExpression closureMethodCallExpression = GrulesASTFactory.createMethodCall(variableName, [IT_VARIABLE])
			ClosureExpression closure = GrulesASTFactory.createSingleExpressionClosure(closureMethodCallExpression)
			GrulesASTFactory.createConstructorCall(FunctionTerm, [closure, new ConstantExpression(variableName)])
		} else 	if (expression instanceof BinaryExpression) {
			BinaryExpression binaryExpression = expression
  	  Expression leftExpression = wrapInClosures(binaryExpression.leftExpression)
			if (GrulesASTUtils.isArrayItemExpression(expression)) {
			  new BinaryExpression(leftExpression, binaryExpression.operation, binaryExpression.rightExpression)
			} else {
				Expression rightExpression = wrapInClosures(binaryExpression.rightExpression)
   			new BinaryExpression(leftExpression, binaryExpression.operation, rightExpression)
			}
		} else if (expression instanceof UnaryMinusExpression) {
		  new UnaryMinusExpression(wrapInClosures((expression as UnaryMinusExpression).expression))
		} else if (expression instanceof BitwiseNegationExpression) {
			new BitwiseNegationExpression(wrapInClosures((expression as BitwiseNegationExpression).expression))
		} else {
			expression
		}
	}
	
	private static Expression liftErrors(Expression expression) {
		if (isAtomExpression(expression)) {
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
		if (isAtomExpression(expression)) {
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
	
	private static boolean isValidRuleParameter(Expression expression) {
		if (expression instanceof ConstantExpression) {
			ConstantExpression methodConstantExpression = expression
			List<Method> inheritedMethods = (Script.methods as List<Method>) + (RulesScriptAPI.methods as List<Method>)
			!(methodConstantExpression.value in inheritedMethods*.name)
		} else {
			expression instanceof GStringExpression || expression instanceof VariableExpression 
		}
	}

	private boolean isValidRuleMethodCallExpression(MethodCallExpression methodCallExpression) {
		Expression objectExpression = methodCallExpression.objectExpression
		Expression method = methodCallExpression.method
		ArgumentListExpression arguments = methodCallExpression.arguments
		boolean validObjectExpression
		boolean validMethod
		boolean validArguments
		if (objectExpression == VariableExpression.THIS_EXPRESSION) {
			validObjectExpression = true
			validMethod = isValidRuleParameter(method)
		} else if (objectExpression instanceof ListExpression) {
			validObjectExpression = true
			if (method instanceof ConstantExpression) {
				String methodName = (method as ConstantExpression).value
				validMethod = methodName ==	MetaClassImpl.CLOSURE_CALL_METHOD
			} else {
				validMethod = false
			}
		} else if (objectExpression instanceof BinaryExpression) {
		  validMethod = true
		  BinaryExpression binaryExpression = objectExpression
			boolean ruleParameterIsValid = isValidRuleParameter(binaryExpression.leftExpression)
		  validObjectExpression = binaryExpression.operation.type == Types.LEFT_SQUARE_BRACKET && ruleParameterIsValid 
		}
		validArguments = arguments.expressions.size() == 1 && isValidRuleExpression(arguments.expressions[0])
		if (!validObjectExpression) {
			log("$objectExpression is not valid rule object expression")
		}
		if (!validMethod) {
		  log("$method is not valid rule method") 
		}
		if (!validArguments) {
  		log("$arguments are not valid rule arguments")
		}
		validObjectExpression && validMethod && validArguments
	}
		
	private static boolean isValidRuleBinaryOperation(Token token) {
		token.type in [Types.LOGICAL_AND,	Types.LOGICAL_OR,	Types.RIGHT_SHIFT, Types.LEFT_SQUARE_BRACKET] 
	}
	
	private static boolean isValidRuleExpression(Expression expression) {
		if (expression instanceof BinaryExpression) {
			if (GrulesASTUtils.isArrayItemExpression(expression)) {
			  isValidRuleExpression((expression as BinaryExpression).leftExpression)
		  }	else {
			  BinaryExpression binaryExpression = expression
			  boolean isValidLeftExpression = isValidRuleExpression(binaryExpression.leftExpression)
			  boolean isValidRightExpression = isValidRuleExpression(binaryExpression.rightExpression)
			  isValidRuleBinaryOperation(binaryExpression.operation) && isValidLeftExpression && isValidRightExpression
		  }  
		} else if (expression instanceof NotExpression) {
		  isValidRuleExpression((expression as NotExpression).expression)
		} else if (expression instanceof BitwiseNegationExpression) {
		  isValidRuleExpression((expression as BitwiseNegationExpression).expression)
		} else {
			[VariableExpression, ClosureExpression, MethodCallExpression].any { Class clazz ->
				clazz.isInstance(expression)
			}
		}
	}
	
	private static MethodCallExpression createRuleApplicationExpression(MethodCallExpression methodCallExpression,
	    Expression rule) {
		Expression objectExpression = methodCallExpression.objectExpression
		ClosureExpression ruleClosureExpression = GrulesASTFactory.createSingleExpressionClosure(rule)
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

	private static boolean isFirstExpressionMethodCall(Expression expression) {
		if (isAtomExpression(expression)) {
			if (expression instanceof MethodCallExpression) {
				((expression as MethodCallExpression).arguments as ArgumentListExpression).expressions.size() == 1
			} else {
				false
			} 
		} else if (expression instanceof BinaryExpression) { 
		  isFirstExpressionMethodCall((expression as BinaryExpression).leftExpression)
		} else {
		  throw new IllegalStateException(expression.class)
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