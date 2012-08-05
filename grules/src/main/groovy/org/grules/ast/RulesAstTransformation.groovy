package org.grules.ast

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.CastExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MapEntryExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.ast.expr.TernaryExpression
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
import org.grules.script.Rule
import org.grules.script.RulesScriptAPI
import org.grules.script.expressions.SubrulesSeq
import org.grules.script.expressions.SubrulesSeqWrapper
import org.grules.utils.AstUtils

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
class RulesAstTransformation extends GrulesAstTransformation {

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
        MethodCallExpression changeGroupMethodCall = ExpressionFactory.createMethodCall(
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
    if (AstUtils.hasAnnotation(declarationExpression, Parameter)) {
      String parameterName = (declarationExpression.leftExpression as VariableExpression).name
      Expression parameterNameExpression = new ConstantExpression(parameterName)
      Expression value = declarationExpression.rightExpression
      ExpressionFactory.createMethodCall(RulesScriptAPI.&addParameter, [parameterNameExpression, value])
    } else if (AstUtils.hasAnnotation(declarationExpression, Rule)) {
      ClosureExpression closureExpression = declarationExpression.rightExpression
      ExpressionStatement ruleStatement = (closureExpression.code as BlockStatement).statements[0]
      Expression ruleExpression = convertToRuleExpression(ruleStatement.expression)
      Expression parameter = {
        if (closureExpression.parameters.length > 0) {
          new VariableExpression(closureExpression.parameters[0])
        } else {
          ExpressionFactory.createItVariable()
        }
      }()
      List<Expression> arguments = [parameter]
      ruleStatement.expression = ExpressionFactory.createMethodCall(ruleExpression, SubrulesSeq.&apply, arguments)
      declarationExpression
    } else {
      declarationExpression
    }
  }

  private Expression transformExpression(MethodCallExpression methodCallExpression) {
    Expression methodExpression = methodCallExpression.method
    String nolog = (RulesScriptAPI.&nolog as MethodClosure).method
    if (methodExpression instanceof ConstantExpression && (methodExpression as ConstantExpression).value == nolog) {
      List<Expression> expressions = (methodCallExpression.arguments as ArgumentListExpression).expressions
      for (int i = 0; i < expressions.size; i++) {
        expressions[i] = new ConstantExpression((expressions[i] as VariableExpression).name)
      }
      return methodCallExpression
    }
    if (!RuleExpressionVerifier.isValidRuleMethodCallExpression(methodCallExpression)) {
      return methodCallExpression
    }
    convertToRuleApplicationExpression(methodCallExpression)
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
   * Parses expression AST tree and transforms it to a rule application expression.
   *
   * @param methodCallExpression method call expression that has to be converted to a rule application expression
   * @return rule application expression
   */
  private Expression convertToRuleApplicationExpression(MethodCallExpression methodCallExpression) {
    ArgumentListExpression arguments = methodCallExpression.arguments
    Expression ruleExpression = convertToRuleExpression(arguments.expressions[0])
    Expression ruleApplicationExpression = createRuleApplicationExpression(methodCallExpression, ruleExpression)
    log('Rule application expression', ruleApplicationExpression)
    ruleApplicationExpression
  }

  /**
   * Parses rule expression AST tree and transforms it to a rule expression.
   *
   * @param ruleExpression rule expression
   * @return converted rule expression
   */
  static Expression convertToRuleExpression(Expression ruleExpression) {
    ruleExpression = RuleExpressionFormTransformer.convertPrecedences(ruleExpression)
    ruleExpression = liftErrors(ruleExpression)
    ruleExpression = convertToRuleOperators(ruleExpression)
    ruleExpression = ClosureWrapper.wrapInClosures(ruleExpression)
    addSequenceWrapper(ruleExpression)
  }

  /**
   * Traverses subrules and for each subrule lifts an error to the top level. 
   * 
   * @param expression an expression where for each subrule an error object is bound to the most right expression
   * @return an expression with lifted errors
   */
  private static Expression liftErrors(Expression expression) {
    if (AstUtils.isRightShift(expression)) {
      BinaryExpression binaryExpression = expression
      Expression leftExpression = liftErrors(binaryExpression.leftExpression)
      Expression rightExpression = liftErrors(binaryExpression.rightExpression)
      new BinaryExpression(leftExpression, binaryExpression.operation, rightExpression)
    } else if (expression instanceof TernaryExpression) {
      TernaryExpression ternaryExpression = expression
      Expression trueExpression = liftErrors(ternaryExpression.trueExpression)
      Expression falseExpression = liftErrors(ternaryExpression.falseExpression)
      new TernaryExpression(ternaryExpression.booleanExpression, trueExpression, falseExpression)
    } else {
      liftError(expression)
    }
  }

  /**
   * Lifts a subrule error to the top level.
   *
   * @param expression an expression where an error object is bound to the most right expression
   * @return a subrule with a lifted error
   */
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
    if (AstUtils.isArrayItemExpression(expression)) {
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
    if (AstUtils.isArrayItemExpression(expression)) {
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
    if (AstUtils.isArrayItemExpression(expression)) {
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
    } else if (AstUtils.isArrayItemExpression(expression)) {
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
    } else if (expression instanceof TernaryExpression) {
      TernaryExpression ternaryExpression = expression
      Expression trueExpression = convertToRuleOperators(ternaryExpression.trueExpression)
      Expression falseExpression = convertToRuleOperators(ternaryExpression.falseExpression)
      new TernaryExpression(ternaryExpression.booleanExpression, trueExpression, falseExpression)
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
    Expression ruleClosureExpression = ExpressionFactory.createClosureExpression(ruleExpression)
    if (objectExpression == VariableExpression.THIS_EXPRESSION) {
      Expression parameterExpression = methodCallExpression.method
      List<Expression> arguments = [parameterExpression, ruleClosureExpression]
      ExpressionFactory.createMethodCall(RulesScriptAPI.&applyRuleToRequiredParameter, arguments)
    } else if (objectExpression instanceof ListExpression) {
      ListExpression listExpression = objectExpression
      List<String> parametersNames = []
      List<Expression> requiredParameters = []
      Map<String, Expression> optionalParameters = [:]
      listExpression.expressions.each {Expression expression ->
        if (AstUtils.isArrayItemExpression(expression)) {
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
      ExpressionFactory.createMethodCall(RulesScriptAPI.&applyRuleToParametersList, arguments)
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
      ExpressionFactory.createMethodCall(RulesScriptAPI.&applyRuleToOptionalParameter, arguments)
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
    if (AstUtils.isRightShift(expression)) {
      BinaryExpression binaryExpression = expression
      Expression leftExpression = addSequenceWrapper(binaryExpression.leftExpression)
      new BinaryExpression(leftExpression, binaryExpression.operation, binaryExpression.rightExpression)
    } else {
      ExpressionFactory.createStaticMethodCall(SubrulesSeqWrapper, SubrulesSeqWrapper.&wrap, [expression])
    }
  }
}