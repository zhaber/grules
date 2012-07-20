package org.grules.ast

import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.VariableScope
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.runtime.MethodClosure

/**
 * A factory for Groovy expressions used by Grules local and global AST transformations.
 */
class GrulesASTFactory {
	
	final static String IT_NAME = 'it'
	final static String CALL_METHOD_NAME = 'call'
	
	/**
	 * Creates an instance method call that calls the given method closure.
	 *
	 * @param methodClosure
	 * @param arguments
	 * @return method call expression
	 */
	static MethodCallExpression createMethodCall(MethodClosure methodClosure,	 List<Expression> arguments) {
		createMethodCall(new ConstantExpression(methodClosure.method), arguments)
	}
	
	/**
	 * Creates an instance method call that calls a method with the given name.
	 *
	 * @param methodName
	 * @param arguments
	 * @return method call expression
	 */
	static MethodCallExpression createMethodCall(String methodName,	 List<Expression> arguments) {
		createMethodCall(new ConstantExpression(methodName), arguments)
	}
	
	/**
	 * Creates an instance method call based the method represented by the given expression.
	 *
	 * @param methodExpression
	 * @param arguments
	 * @return method call expression
	 */
	static MethodCallExpression createMethodCall(Expression methodExpression,	 List<Expression> arguments) {
		Expression argumentsExpression = new ArgumentListExpression(arguments)
		new MethodCallExpression(VariableExpression.THIS_EXPRESSION, methodExpression, argumentsExpression)
	}
	
	/**
	 * Creates a static method call that calls the given method closure.
	 *
	 * @param clazz owner class
	 * @param methodClosure
	 * @param arguments
	 * @return method call expression
	 */
	static MethodCallExpression createStaticMethodCall(Class clazz, MethodClosure methodClosure,
			List<Expression> arguments) {
		Expression argumentsExpression = new ArgumentListExpression(arguments)
		Expression methodExpression = new ConstantExpression(methodClosure.method)
		new MethodCallExpression (new ClassExpression(ClassHelper.make(clazz)), methodExpression,	 argumentsExpression)
	}
			
	/**
	 * Creates a constructor call for the given class and arguments.
	 *
	 * @param clazz owner class
	 * @param arguments constructor arguments
	 * @return constructor call expression
	 */
	static ConstructorCallExpression createConstructorCall(Class clazz, List<Expression> arguments) {
		Expression argumentsExpression = new ArgumentListExpression(arguments)
		new ConstructorCallExpression(ClassHelper.make(clazz), argumentsExpression)
	}

	/**
	 * Creates closure with a single expression.
	 * 
	 * @param expression a Groovy expression that has to be wrapped in closure
	 * @return closure expression that wraps the specified expression
	 */
	static ClosureExpression createClosureExpression(Expression expression) {
		Statement blockStatement = new BlockStatement([new ExpressionStatement(expression)], new VariableScope())
		new ClosureExpression(new Parameter[0], blockStatement)
	}
	
	/**
	 * Creates closure with the specified block statement.
	 * 
	 * @param blockStatement a block statement that has to be wrapped in closure
	 * @return closure expression that wraps the specified block statement
	 */
	static 	ClosureExpression createClosureExpression(BlockStatement blockStatement) {
		VariableScope closureVariableScope = blockStatement.scope.copy()
		closureVariableScope.parent = blockStatement.scope
		List<Statement> statements = blockStatement.statements
		VariableScope closureBlockVariableScope = closureVariableScope.copy()
		closureBlockVariableScope.parent = closureVariableScope
		Statement closureBlockStatement = new BlockStatement(statements, closureBlockVariableScope)
		ClosureExpression closureExpression = new ClosureExpression(new Parameter[0], closureBlockStatement)
		closureExpression.variableScope = closureVariableScope
		closureExpression
	}
	
	/**
	 * Creates a variable "it".
	 * 
	 * @return variable "it" expression
	 */
	static 	VariableExpression createItVariable() {
		new VariableExpression(IT_NAME)
	}
}