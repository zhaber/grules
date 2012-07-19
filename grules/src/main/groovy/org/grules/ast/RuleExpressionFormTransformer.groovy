package org.grules.ast

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types


class RuleExpressionFormTransformer {
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
	static List<Object> infixToPostfixExpression(List<Object> infixExpression){
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
	static Expression postfixExpressionToTree(Deque postfixExpression) {
		Stack<Object> stack = [] as Stack
		while (!postfixExpression.isEmpty()) {
			def expression = postfixExpression.removeFirst()
			if (expression instanceof Expression) {
				 stack << expression
			} else if (expression instanceof Token) {
				Token token = expression
				if (RuleExpressionVerifier.isValidRuleBinaryOperation(token)) {
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

	/**
	 * Traverses expressions child nodes and converts them to infix notation.
	 */
	private static List transformChildExpressionsToInfix(Expression expression, Token operation) {
		Integer operationPrecedence = GrulesASTUtils.fetchPrecedence(operation)
		if (expression instanceof BinaryExpression) {
			BinaryExpression binaryExpression = expression
			List leftExpression = transformToInfixExpression(binaryExpression.leftExpression, operationPrecedence)
			List rightExpression = transformToInfixExpression(binaryExpression.rightExpression, operationPrecedence)
			leftExpression + [operation] + rightExpression
		} else if (expression instanceof NotExpression) {
			NotExpression notExpression = expression
			List innerExpression = transformToInfixExpression(notExpression.expression, operationPrecedence)
			[operation] + innerExpression
		} else if (expression instanceof BitwiseNegationExpression) {
			BitwiseNegationExpression bitwiseExpression = expression
			List innerExpression = transformToInfixExpression(bitwiseExpression.expression, operationPrecedence)
			[operation] + innerExpression
		} else {
			throw new UnsupportedExpressionException(expression.class)
		}
	}
	
	/**
	 * Traverses rule expression AST inorder and converts to infix notation. Parentheses are added when necessary.
	 *
	 * @param expression rule expression
	 * @return rule expression in expression tree form
	 */
	static List transformToInfixExpression(Expression expression, Integer maxPrecedence = Integer.MIN_VALUE) {
		if (RuleExpressionVerifier.isAtomExpression(expression)) {
			return [expression]
		}
		Token operation = GrulesASTUtils.fetchOperationToken(expression)
		List infixExpression = transformChildExpressionsToInfix(expression, operation)
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

}
