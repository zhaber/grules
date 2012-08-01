package org.grules.ast

import java.lang.reflect.Method

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
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types
import org.grules.script.RulesScriptAPI
import org.grules.utils.AstUtils


/**
 * A helper class that checks validity of a rule expression.
 */
class RuleExpressionVerifier {

  static boolean isValidRuleBinaryOperation(Token token) {
    token.type in [Types.LOGICAL_AND,	Types.LOGICAL_OR,	Types.RIGHT_SHIFT, Types.LEFT_SQUARE_BRACKET]
  }

  static boolean isAtomExpression(Expression expression) {
    if (expression instanceof BinaryExpression) {
      BinaryExpression binaryExpression = expression
      AstUtils.isArrayItemExpression(binaryExpression) && isAtomExpression(binaryExpression.leftExpression)
    } else {
      !(expression instanceof NotExpression) && !(expression instanceof BitwiseNegationExpression)
    }
  }

  static boolean isFirstExpressionMethodCall(Expression expression) {
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

  private static boolean isValidRuleApplicationMethodNameExpression(Expression expression) {
    if (expression instanceof ConstantExpression) {
      List<Method> inheritedMethods = (Script.methods as List<Method>) + (RulesScriptAPI.methods as List<Method>)
      !((expression as ConstantExpression).value in inheritedMethods*.name)
    } else {
      isValidParameterNameExpression(expression)
    }
  }

  private static boolean isValidParameterNameExpression(Expression expression) {
    expression instanceof GStringExpression || expression instanceof VariableExpression
  }

  static boolean isValidRuleMethodCallExpression(MethodCallExpression methodCallExpression) {
    Expression objectExpression = methodCallExpression.objectExpression
    Expression method = methodCallExpression.method
    ArgumentListExpression arguments = methodCallExpression.arguments
    boolean validObjectExpression
    boolean validMethod
    boolean validArguments
    if (objectExpression == VariableExpression.THIS_EXPRESSION) {
      validObjectExpression = true
      validMethod = isValidRuleApplicationMethodNameExpression(method)
    } else if (objectExpression instanceof ListExpression) {
      ListExpression listExpression = objectExpression
      validObjectExpression = listExpression.expressions.every {Expression expression ->
        if (isValidParameterNameExpression(expression)) {
          true
        } else if (expression instanceof BinaryExpression) {
          isValidParameterBinaryExpression(expression)
        }
      }
      if (method instanceof ConstantExpression) {
        String methodName = (method as ConstantExpression).value
        validMethod = methodName ==	MetaClassImpl.CLOSURE_CALL_METHOD
      } else {
        validMethod = false
      }
    } else if (objectExpression instanceof BinaryExpression) {
      validMethod = true
      validObjectExpression = isValidParameterBinaryExpression(objectExpression)
    }
    validArguments = arguments.expressions.size() == 1 && isValidRuleExpression(arguments.expressions[0])
    validObjectExpression && validMethod && validArguments
  }

  private static boolean isValidParameterBinaryExpression(BinaryExpression parameterExpression) {
    boolean ruleParameterIsValid = isValidParameterNameExpression(parameterExpression.leftExpression)
    parameterExpression.operation.type == Types.LEFT_SQUARE_BRACKET && ruleParameterIsValid
  }

  static boolean isValidRuleExpression(Expression expression) {
    if (expression instanceof BinaryExpression) {
      if (AstUtils.isArrayItemExpression(expression)) {
        isValidRuleExpression((expression as BinaryExpression).leftExpression)
      }	else {
        BinaryExpression binaryExpression = expression
        boolean isValidLeftExpression = isValidRuleExpression(binaryExpression.leftExpression)
        boolean isValidRightExpression = isValidRuleExpression(binaryExpression.rightExpression)
        boolean isValidBinaryOperation = RuleExpressionVerifier.isValidRuleBinaryOperation(binaryExpression.operation)
        isValidBinaryOperation && isValidLeftExpression && isValidRightExpression
      }
    } else if (expression instanceof NotExpression) {
      isValidRuleExpression((expression as NotExpression).expression)
    } else if (expression instanceof BitwiseNegationExpression) {
      isValidRuleExpression((expression as BitwiseNegationExpression).expression)
    } else {
      [VariableExpression, ClosureExpression, MethodCallExpression, GStringExpression].any { Class clazz ->
        clazz.isInstance(expression)
      }
    }
  }
}

