package org.grules.ast

import groovy.inspect.swingui.AstNodeToScriptVisitor

import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.UnaryMinusExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.runtime.MethodClosure
import org.grules.script.RulesScriptAPI
import org.grules.script.expressions.FunctionTerm
import org.grules.utils.AstUtils

/**
 * Wraps method calls into closures.
 */
class ClosureWrapper {

  /**
   * Converts a method expression to string.
   */
  private static ConstantExpression methodToConstantExpression(Expression methodExpression) {
    if (methodExpression instanceof ConstantExpression) {
      return methodExpression
    }
    Writer stringWriter = new StringWriter()
    AstNodeToScriptVisitor stringAstNodeToScriptVisitor = new AstNodeToScriptVisitor(stringWriter)
    methodExpression.visit(stringAstNodeToScriptVisitor)
    new ConstantExpression(stringWriter.toString())
  }

  private static boolean isSkipFunction(Expression expression) {
    String skipMethodName = (RulesScriptAPI.&skip as MethodClosure).method
    expression instanceof ConstantExpression && (expression as ConstantExpression).value == skipMethodName
  }

  /**
   * Wraps a method call into a closure.
   */
  static Expression wrapInClosures(MethodCallExpression expression) {
    Expression methodExpression = expression.method
    if (isSkipFunction(methodExpression)) {
      return expression
    }
    List<Expression> arguments = (expression.arguments as ArgumentListExpression).expressions
    arguments = [ExpressionFactory.createItVariable()] + arguments
    Expression closureMethodCallExpression = ExpressionFactory.createMethodCall(methodExpression, arguments)
    Expression closure = ExpressionFactory.createClosureExpression(closureMethodCallExpression)
    ExpressionFactory.createConstructorCall(FunctionTerm, [closure, methodToConstantExpression(methodExpression)])
  }

  /**
   * Creates a method call with a name of the given variable and wraps it into a closure.
   */
  static Expression wrapInClosures(VariableExpression expression) {
    String variableName = (expression as VariableExpression).name
    List<Expression> arguments = [ExpressionFactory.createItVariable()]
    Expression closureMethodCallExpression = ExpressionFactory.createMethodCall(variableName, arguments)
    Expression closure = ExpressionFactory.createClosureExpression(closureMethodCallExpression)
    ExpressionFactory.createConstructorCall(FunctionTerm, [closure, new ConstantExpression(variableName)])
  }

  /**
   * Creates a method call with a name of the passed string and wraps it into a closure.
   */
  static Expression wrapInClosures(GStringExpression expression) {
    Expression itVariable = ExpressionFactory.createItVariable()
    Expression closureMethodCallExpression = ExpressionFactory.createMethodCall(expression, [itVariable])
    Expression closure = ExpressionFactory.createClosureExpression(closureMethodCallExpression)
    ExpressionFactory.createConstructorCall(FunctionTerm, [closure, expression])
  }

  /**
   * Traverses a binary expression and wraps its leaves into closures.
   */
  static Expression wrapInClosures(BinaryExpression expression) {
    Expression leftExpression = wrapInClosures(expression.leftExpression)
    if (AstUtils.isArrayItemExpression(expression)) {
      new BinaryExpression(leftExpression, expression.operation, expression.rightExpression)
    } else {
      Expression rightExpression = wrapInClosures(expression.rightExpression)
       new BinaryExpression(leftExpression, expression.operation, rightExpression)
    }
  }

  /**
   * Wraps an inner expression of an unary minus expression into a closure.
   */
  static Expression wrapInClosures(UnaryMinusExpression expression) {
    new UnaryMinusExpression(wrapInClosures((expression as UnaryMinusExpression).expression))
  }

  /**
   * Wraps an inner expression of a bitwise negation expression into a closure.
   */
  static Expression wrapInClosures(BitwiseNegationExpression expression) {
    new BitwiseNegationExpression(wrapInClosures((expression as BitwiseNegationExpression).expression))
  }

  /**
   * Expressions of unsupported types are not wrapped.
   */
  static Expression wrapInClosures(Expression expression) {
    expression
  }
}
