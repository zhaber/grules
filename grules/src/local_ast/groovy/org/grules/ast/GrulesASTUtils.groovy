package org.grules.ast

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types

class GrulesASTUtils {

	/**
	 * Returns token precedence.
	 */
  static Integer fetchPrecedence(Token token) {
		Types.getPrecedence(token.type, true)
	}
	
	/**
	 * Returns a token object for operation used in the specified expression on the most top level. 
	 */
	static Token fetchOperationToken(Expression expression) {
		Integer lineNumber = expression.lineNumber
		Integer columnNumber = expression.columnNumber
		if (expression instanceof BinaryExpression) {
			(expression as BinaryExpression).operation
		} else if (expression instanceof NotExpression) {
			new Token(Types.NOT, Types.getText(Types.NOT), lineNumber, columnNumber)
		} else if (expression instanceof BitwiseNegationExpression) {
			new Token(Types.BITWISE_NEGATION, Types.getText(Types.BITWISE_NEGATION), lineNumber, columnNumber)
		} else {
			throw new UnsupportedExpressionException(expression.class)
		}
	}
	
	/**
	 * Checks if the specified expression is an array item expression.
	 */
	static boolean isArrayItemExpression(Expression expression) {
		if (expression instanceof BinaryExpression) {
			(expression as BinaryExpression).operation.type == Types.LEFT_SQUARE_BRACKET
		} else {
			false
		}
	}
	
	/**
	 * Checks if the specified expression is a right shift expression.
	 */
	static boolean isRightShift(Expression expression) {
		if (expression instanceof BinaryExpression) {
			(expression as BinaryExpression).operation.type == Types.RIGHT_SHIFT
		} else {
			false
		}
	}
  
}
