package org.grules.ast;

import groovy.lang.Script;

import java.lang.reflect.Method;
import java.util.List;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;
import org.grules.GroovyConstants;
import org.grules.script.RulesScriptAPI;
import org.grules.utils.AstUtils;

import com.google.common.collect.ImmutableSet;

/**
 * A helper class that checks validity of a rule expression.
 */
class RuleExpressionVerifier {

  static boolean isValidRuleBinaryOperation(Token token) {
    ImmutableSet<Integer> binaryOpertions = ImmutableSet.of(
        Types.LOGICAL_AND,
        Types.LOGICAL_OR,
        Types.RIGHT_SHIFT,
        Types.LEFT_SQUARE_BRACKET);
    return binaryOpertions.contains(token.getType());
  }

  static boolean isAtomExpression(Expression expression) {
    if (expression instanceof BinaryExpression) {
      BinaryExpression binaryExpression = (BinaryExpression) expression;
      return AstUtils.isArrayItemExpression(binaryExpression) && isAtomExpression(binaryExpression.getLeftExpression());
    } else {
      return !(expression instanceof NotExpression) && !(expression instanceof BitwiseNegationExpression);
    }
  }

  static boolean isFirstExpressionMethodCall(Expression expression) {
    if (isAtomExpression(expression)) {
      if (expression instanceof MethodCallExpression) {
        MethodCallExpression methodCallExpression = (MethodCallExpression)expression;
        return ((ArgumentListExpression)methodCallExpression.getArguments()).getExpressions().size() == 1;
      } else {
        return false;
      }
    } else if (expression instanceof BinaryExpression) {
      return isFirstExpressionMethodCall(((BinaryExpression)expression).getLeftExpression());
    } else {
      return false;
    }
  }

  private static boolean isValidRuleApplicationMethodNameExpression(Expression expression) {
    if (expression instanceof ConstantExpression) {
      for (Class<?> inheritedType : ImmutableSet.of(Script.class, RulesScriptAPI.class)) {
        for (Method method : inheritedType.getMethods()) {
          if (method.getName().equals((((ConstantExpression)expression).getValue()))) {
            return false;
          }
        }
      }
      return true;
    } else {
      return isValidParameterNameExpression(expression);
    }
  }

  private static boolean isValidParameterNameExpression(Expression expression) {
    return expression instanceof GStringExpression || expression instanceof VariableExpression;
  }

  private static boolean isValidListRuleExpression(List<Expression> expressions) {
    for (Expression expression : expressions) {
      if (!isValidParameterNameExpression(expression) &&
          !(expression instanceof BinaryExpression &&
          isValidParameterBinaryExpression((BinaryExpression)expression))) {
        return false;
      }
    }
    return true;
  }

  static boolean isValidRuleMethodCallExpression(MethodCallExpression methodCallExpression) {
    Expression objectExpression = methodCallExpression.getObjectExpression();
    Expression method = methodCallExpression.getMethod();
    ArgumentListExpression arguments = (ArgumentListExpression) methodCallExpression.getArguments();
    boolean validObjectExpression = false;
    boolean validMethod = false;
    boolean validArguments;
    if (objectExpression == VariableExpression.THIS_EXPRESSION) {
      validObjectExpression = true;
      validMethod = isValidRuleApplicationMethodNameExpression(method);
    } else if (objectExpression instanceof ListExpression) {
      ListExpression listExpression = (ListExpression) objectExpression;
      validObjectExpression = isValidListRuleExpression(listExpression.getExpressions());
      if (method instanceof ConstantExpression) {
        validMethod = ((ConstantExpression)method).getValue().equals(GroovyConstants.CALL_METHOD_NAME);
      } else {
        validMethod = false;
      }
    } else if (objectExpression instanceof BinaryExpression) {
      validMethod = true;
      validObjectExpression = isValidParameterBinaryExpression((BinaryExpression) objectExpression);
    }
    validArguments = arguments.getExpressions().size() == 1 && isValidRuleExpression(arguments.getExpressions().get(0));
    return validObjectExpression && validMethod && validArguments;
  }

  private static boolean isValidParameterBinaryExpression(BinaryExpression parameterExpression) {
    boolean ruleParameterIsValid = isValidParameterNameExpression(parameterExpression.getLeftExpression());
    return parameterExpression.getOperation().getType() == Types.LEFT_SQUARE_BRACKET && ruleParameterIsValid;
  }

  static boolean isValidRuleExpression(Expression expression) {
    if (expression instanceof BinaryExpression) {
      if (AstUtils.isArrayItemExpression(expression)) {
        return isValidRuleExpression(((BinaryExpression)expression).getLeftExpression());
      }	else {
        BinaryExpression binaryExpression = (BinaryExpression) expression;
        boolean isValidLeftExpression = isValidRuleExpression(binaryExpression.getLeftExpression());
        boolean isValidRightExpression = isValidRuleExpression(binaryExpression.getRightExpression());
        boolean isValidBinaryOperation = RuleExpressionVerifier.isValidRuleBinaryOperation(binaryExpression.getOperation());
        return isValidBinaryOperation && isValidLeftExpression && isValidRightExpression;
      }
    } else if (expression instanceof NotExpression) {
      return isValidRuleExpression(((NotExpression)expression).getExpression());
    } else if (expression instanceof BitwiseNegationExpression) {
      return isValidRuleExpression(((BitwiseNegationExpression)expression).getExpression());
    } else if (expression instanceof TernaryExpression) {
      TernaryExpression ternaryExpression = (TernaryExpression) expression;
      Boolean isValidRuleTrueExpression = isValidRuleExpression(ternaryExpression.getTrueExpression());
      Boolean isValidRuleFalseExpression = isValidRuleExpression(ternaryExpression.getFalseExpression());
      return isValidRuleTrueExpression && isValidRuleFalseExpression;
    } else {
      ImmutableSet<Class<? extends Expression>> ruleExpressionClasses = ImmutableSet.of(
          VariableExpression.class,
          ClosureExpression.class,
          MethodCallExpression.class,
          GStringExpression.class);
      for (Class<?> ruleExpressionClass : ruleExpressionClasses) {
        if (ruleExpressionClass.isInstance(expression)) {
          return true;
        }
      }
      return false;
    }
  }
}