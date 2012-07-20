package org.grules.ast

import groovy.inspect.swingui.AstNodeToScriptVisitor

import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.UnaryMinusExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.grules.script.expressions.FunctionTerm

/**
 * Wraps method calls into closures.
 */
class ClosureWrapper {

	static Expression wrapInClosures(Expression expression) {
		expression
	}
	
	static Expression wrapInClosures(MethodCallExpression expression) {
		List<Expression> arguments = (expression.arguments as ArgumentListExpression).expressions
		arguments = [GrulesASTFactory.createItVariable()] + arguments 
		Expression method = expression.method
		Expression closureMethodCallExpression = GrulesASTFactory.createMethodCall(method, arguments)
	  Expression closure = GrulesASTFactory.createClosureExpression(closureMethodCallExpression)
	  Writer stringWriter = new StringWriter()
	  AstNodeToScriptVisitor stringAstNodeToScriptVisitor = new AstNodeToScriptVisitor(stringWriter)
	  method.visit(stringAstNodeToScriptVisitor)
		Expression methodName = new ConstantExpression(stringWriter.toString())
	  GrulesASTFactory.createConstructorCall(FunctionTerm, [closure, methodName])
	}
	
  static Expression wrapInClosures(VariableExpression expression) {
	  String variableName = (expression as VariableExpression).name 
		List<Expression> arguments = [GrulesASTFactory.createItVariable()]
	  Expression closureMethodCallExpression = GrulesASTFactory.createMethodCall(variableName, arguments)
	  Expression closure = GrulesASTFactory.createClosureExpression(closureMethodCallExpression)
	  GrulesASTFactory.createConstructorCall(FunctionTerm, [closure, new ConstantExpression(variableName)])
  }
		
	static Expression wrapInClosures(GStringExpression expression) {
		Expression itVariable = GrulesASTFactory.createItVariable()
		Expression closureMethodCallExpression = GrulesASTFactory.createMethodCall(expression, [itVariable])
	  Expression closure = GrulesASTFactory.createClosureExpression(closureMethodCallExpression)
	  GrulesASTFactory.createConstructorCall(FunctionTerm, [closure, expression])
	}
		
	static Expression wrapInClosures(BinaryExpression expression) {
  	Expression leftExpression = wrapInClosures(expression.leftExpression)
		if (GrulesASTUtils.isArrayItemExpression(expression)) {
		  new BinaryExpression(leftExpression, expression.operation, expression.rightExpression)
		} else {
		  Expression rightExpression = wrapInClosures(expression.rightExpression)
   	  new BinaryExpression(leftExpression, expression.operation, rightExpression)
	  }
	}
		 
	static Expression wrapInClosures(UnaryMinusExpression expression) {
	  new UnaryMinusExpression(wrapInClosures((expression as UnaryMinusExpression).expression))
	}
		
	static Expression wrapInClosures(BitwiseNegationExpression expression) {
	  new BitwiseNegationExpression(wrapInClosures((expression as BitwiseNegationExpression).expression))
	}
}