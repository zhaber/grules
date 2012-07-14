package org.grules.ast

import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.syntax.Token

class ASTTestUtils {

	static convertPrecedences(ruleExpression) {
		ruleExpression = RulesASTTransformation.convertToInfixExpression(ruleExpression)
		ruleExpression = RulesASTTransformation.infixToPostfixExpression(ruleExpression)
		RulesASTTransformation.postfixExpressionToTree(ruleExpression)
	}
	
	static List<Expression> fetchArguments(MethodCallExpression methodCallExpression) {
		(methodCallExpression.arguments as ArgumentListExpression).expressions
	}
	
	static List<Expression> fetchArguments(ConstructorCallExpression constructorCallExpression) {
		(constructorCallExpression.arguments as ArgumentListExpression).expressions
	}
	
	static Expression fetchClosureExpression(ClosureExpression closureExpression) {
		((closureExpression.code as BlockStatement).statements[0] as ExpressionStatement).expression
	}

	static boolean checkToken(originalValue, Integer type) {
		if (!(originalValue instanceof Token)) {
			return false
		}
		(originalValue as Token).type == type
	}
	
	static boolean checkVariable(originalValue, variableName) {
		if (!(originalValue instanceof VariableExpression)) {
			return false
		}
		(originalValue as VariableExpression).name == variableName
	}
	
	static Expression fetchStatementBlocksExpression(List<BlockStatement> statementBlocks) {
		(statementBlocks[0].statements[0] as ExpressionStatement).expression
	}

}
