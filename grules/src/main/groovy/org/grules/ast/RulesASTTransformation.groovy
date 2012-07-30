package org.grules.ast

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.CastExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MapEntryExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.ast.expr.UnaryMinusExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.runtime.MethodClosure
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.grules.Grules
import org.grules.functions.lib.CommonFunctions
import org.grules.functions.lib.DateFunctions
import org.grules.functions.lib.MathFunctions
import org.grules.functions.lib.SecurityFunctions
import org.grules.functions.lib.StringFunctions
import org.grules.functions.lib.TypeFunctions
import org.grules.functions.lib.UserFunctions
import org.grules.script.Parameter
import org.grules.script.RulesScriptAPI
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

  private static final List<Class> IMPORT_CLASSES = [CommonFunctions, DateFunctions, StringFunctions, TypeFunctions,
      UserFunctions, MathFunctions, SecurityFunctions]
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
    for (int i = 0; i < statements.size; i++) {
      Statement statement = statements[i]
      if (statement.statementLabel != null) {
        log('Creating method call for group', statement.statementLabel)
        MethodCallExpression changeGroupMethodCall = GrulesASTFactory.createMethodCall(
            RulesScriptAPI.&changeGroup, [new ConstantExpression(statement.statementLabel)])
        statements.add(i++, new ExpressionStatement(changeGroupMethodCall))
        log('Added method call', (RulesScriptAPI.&changeGroup as MethodClosure).method +
            "($statement.statementLabel)")
      }
      visitStatement(statement)
    }
  }

  private void visitStatement(Statement statement) {
    log("Skipping statement $statement")
  }

  private void visitStatement(IfStatement ifStatement) {
    visitStatement(ifStatement.ifBlock)
    visitStatement(ifStatement.elseBlock)
  }

  private void visitStatement(BlockStatement blockStatement) {
    blockStatement.statements.each {Statement statement ->
      visitStatement(statement)
    }
  }

  private Expression transformExpression(Expression expression) {
    expression
  }

  private Expression transformExpression(DeclarationExpression declarationExpression) {
    boolean isParameter = declarationExpression.annotations.any {AnnotationNode annotationNode ->
      annotationNode.classNode.name == Parameter.simpleName
    }
    if (isParameter) {
      String parameterName = (declarationExpression.leftExpression as VariableExpression).name
      Expression parameterNameExpression = new ConstantExpression(parameterName)
      Expression value = declarationExpression.rightExpression
      GrulesASTFactory.createMethodCall(RulesScriptAPI.&addParameter, [parameterNameExpression, value])
    } else {
      declarationExpression
    }
  }

  private Expression transformExpression(MethodCallExpression methodCallExpression) {
    if (!RuleExpressionVerifier.isValidRuleMethodCallExpression(methodCallExpression)) {
      return methodCallExpression
    }
    convertToRuleExpression(methodCallExpression)
  }

  private Expression transformExpression(BinaryExpression binaryExpression) {
    if (!RuleExpressionVerifier.isFirstExpressionMethodCall(binaryExpression)) {
      return binaryExpression
    }
    BinaryExpression firstMethodCallBinaryExpression = fetchFirstMethodCallBinaryExpression(binaryExpression)
    MethodCallExpression methodCallExpression = firstMethodCallBinaryExpression.leftExpression
    ArgumentListExpression argumentListExpression = methodCallExpression.arguments
    firstMethodCallBinaryExpression.leftExpression = argumentListExpression.expressions[0]
    argumentListExpression.expressions[0] = binaryExpression
    transformExpression(methodCallExpression)
  }

  private void visitStatement(ExpressionStatement statement) {
    statement.expression = transformExpression(statement.expression)
  }

  /**
   * Parses rule expression AST tree and makes precedence of right shift operator lower than conjunction and
   * disjunction.
   *
   * @param infixExpression subrules sequence in infix form
   * @return converted rule expression with correct operator precedence
   */
  private Expression convertToRuleExpression(MethodCallExpression methodCallExpression) {
    Expression ruleExpression = (methodCallExpression.arguments as ArgumentListExpression).expressions[0]
    log('Original rule', ruleExpression)
    ruleExpression = RuleExpressionFormTransformer.convertPrecedences(ruleExpression)
    log('Rule with changed precedences of &, |, and >> operators', ruleExpression)
    ruleExpression = liftErrors(ruleExpression)
    ruleExpression = convertToRuleOperators(ruleExpression)
    ruleExpression = ClosureWrapper.wrapInClosures(ruleExpression)
    ruleExpression = addSequenceWrapper(ruleExpression)
    Expression ruleApplicationExpression = createRuleApplicationExpression(methodCallExpression, ruleExpression)
    log('Rule application expression', ruleApplicationExpression)
    ruleApplicationExpression
  }

  private static Expression liftErrors(Expression expression) {
    if (RuleExpressionVerifier.isAtomExpression(expression)) {
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
    if (RuleExpressionVerifier.isAtomExpression(expression)) {
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

  private static String fetchParameterName(VariableExpression expression) {
    expression.name
  }

  private static String fetchParameterName(GStringExpression expression) {
    expression.text.replaceFirst(/^\$/, '')
  }

  private static Expression createRuleApplicationExpression(MethodCallExpression methodCallExpression,
      Expression ruleExpression) {
    Expression objectExpression = methodCallExpression.objectExpression
    Expression ruleClosureExpression = GrulesASTFactory.createClosureExpression(ruleExpression)
    if (objectExpression == VariableExpression.THIS_EXPRESSION) {
      Expression parameterExpression = methodCallExpression.method
      List<Expression> arguments = [parameterExpression, ruleClosureExpression]
      GrulesASTFactory.createMethodCall(RulesScriptAPI.&applyRuleToRequiredParameter, arguments)
    } else if (objectExpression instanceof ListExpression) {
      ListExpression listExpression = objectExpression
      List<String> parametersNames = []
      List<Expression> requiredParameters = []
      Map<String, Expression> optionalParameters = [:]
      listExpression.expressions.each {Expression expression ->
        if (GrulesASTUtils.isArrayItemExpression(expression)) {
          BinaryExpression binaryExpression = expression
          Expression parameterNameExpression = binaryExpression.leftExpression
          parametersNames << fetchParameterName(parameterNameExpression)
          optionalParameters << [(parameterNameExpression): binaryExpression.rightExpression]
        } else {
          parametersNames << fetchParameterName(expression)
          requiredParameters << expression
        }
      }
      Expression ruleNameExpression = new ConstantExpression(parametersNames.join(Grules.COMBINED_PARAMETERS_SEPARATOR))
      List<Expression> requiredParametersExpressions = requiredParameters.collect {Expression parameterNameExpression ->
        if (parameterNameExpression instanceof VariableExpression) {
          new ConstantExpression((parameterNameExpression as VariableExpression).name)
        } else if (parameterNameExpression instanceof GStringExpression) {
          parameterNameExpression
        } else {
          throw new IllegalStateException(parameterNameExpression.class)
        }
      }
      Expression requiredParametersListExpression = new ListExpression(requiredParametersExpressions)
      ClassNode setClassNode = ClassHelper.make(Set)
      Expression requiredParametersExpression = new CastExpression(setClassNode, requiredParametersListExpression)
      List<MapEntryExpression> optionalParametersMapEntryExpressions = optionalParameters.collect {
        Expression parameterNameExpression, Expression valueExpression ->
        new MapEntryExpression(parameterNameExpression, valueExpression)
      }
      Expression optionalParametersExpression = new MapExpression(optionalParametersMapEntryExpressions)
      List<Expression> arguments = [ruleNameExpression, requiredParametersExpression, optionalParametersExpression,
          ruleClosureExpression]
      GrulesASTFactory.createMethodCall(RulesScriptAPI.&applyRuleToParametersList, arguments)
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
