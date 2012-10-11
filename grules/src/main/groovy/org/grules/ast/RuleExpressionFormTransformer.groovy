package org.grules.ast

import groovy.transform.TupleConstructor

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types
import org.grules.utils.AstUtils

/**
 * Transforms a rule expression between tree, infix, and postfix forms.
 */
class RuleExpressionFormTransformer {

  /**
   * Converts precedences of the ||, &&, and >> operators
   *
   * @param ruleExpression a rule expression where operators have standard precedence
   * @return a converted rule expression
   */
  static Expression convertPrecedences(Expression ruleExpression) {
    List infixRuleExpression = transformToInfixExpression(ruleExpression)
    List postfixRuleExpression = infixToPostfixExpression(infixRuleExpression)
    postfixExpressionToTree(postfixRuleExpression)
  }

  /**
   * Returns operators precedence in rule expressions.
   */
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
  private static List infixToPostfixExpression(List infixExpression){
    Stack<Token> stack = [] as Stack
    Deque<Object> postfixExpression = [] as Queue
    infixExpression.each { token ->
      if (postfixExpression instanceof TernaryRuleExpression) {
        TernaryRuleExpression ternaryExpression = token
        List trueExpression = infixToPostfixExpression(ternaryExpression.trueExpression)
        List falseExpression = infixToPostfixExpression(ternaryExpression.falseExpression)
        Expression booleanExpression = ternaryExpression.booleanExpression
        postfixExpression << new TernaryRuleExpression(booleanExpression, trueExpression, falseExpression)
      } else if (token instanceof Expression) {
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
      if (expression instanceof TernaryRuleExpression) {
        TernaryRuleExpression ternaryExpression = expression
        Expression trueExpression = postfixExpressionToTree(ternaryExpression.trueExpression)
        Expression falseExpression = postfixExpressionToTree(ternaryExpression.falseExpression)
        stack << new TernaryExpression(ternaryExpression.booleanExpression, trueExpression, falseExpression)
      } else if (expression instanceof Expression) {
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
   * Traverses binary expression's child nodes and converts them to infix notation.
   */
  private static List transformChildExpressionsToInfix(BinaryExpression binaryExpression, Token operation,
      Integer operationPrecedence) {
    List leftExpression = transformToInfixExpression(binaryExpression.leftExpression, operationPrecedence)
    List rightExpression = transformToInfixExpression(binaryExpression.rightExpression, operationPrecedence)
    leftExpression + [operation] + rightExpression
  }

  /**
   * Traverses not expression's child nodes and converts them to infix notation.
   */
  private static List transformChildExpressionsToInfix(NotExpression notExpression, Token operation,
      Integer operationPrecedence) {
    List innerExpression = transformToInfixExpression(notExpression.expression, operationPrecedence)
    [operation] + innerExpression
  }

  /**
   * Traverses bitwise negation expression's child nodes and converts them to infix notation.
   */
  private static List transformChildExpressionsToInfix(BitwiseNegationExpression bitwiseNegationExpression,
       Token operation, Integer operationPrecedence) {
     List innerExpression = transformToInfixExpression(bitwiseNegationExpression.expression, operationPrecedence)
     [operation] + innerExpression
  }

  /**
   * Traverses ternary expression's child nodes and converts them to infix notation.
   */
  private static List transformChildExpressionsToInfix(TernaryExpression ternaryExpression, Token ignored,
      Integer operationPrecedence) {
    List infixTrueExpression = transformToInfixExpression(ternaryExpression.trueExpression, operationPrecedence)
    List infixFalseExpression = transformToInfixExpression(ternaryExpression.falseExpression, operationPrecedence)
    [new TernaryRuleExpression(ternaryExpression.booleanExpression, infixTrueExpression, infixFalseExpression)]
  }

  /**
   * Traverses rule expression AST inorder and converts to infix notation. Parentheses are added when necessary.
   *
   * @param expression rule expression
   * @return rule expression in expression tree form
   */
  private static List transformToInfixExpression(Expression expression, Integer maxPrecedence = Integer.MIN_VALUE) {
    if (RuleExpressionVerifier.isAtomExpression(expression)) {
      return [expression]
    }
    Token operation = AstUtils.fetchOperationToken(expression)
    Integer operationPrecedence = AstUtils.fetchPrecedence(operation)
    List infixExpression = transformChildExpressionsToInfix(expression, operation, operationPrecedence)
    if (AstUtils.fetchPrecedence(operation) >= maxPrecedence) {
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

@TupleConstructor
class TernaryRuleExpression {
  final Expression booleanExpression
  final List trueExpression
  final List falseExpression
}