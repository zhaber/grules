package org.grules.ast

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.grules.functions.ConverterBooleanResult

/**
 * Transformation for a converter function.
 *
 * @see Converter
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class ConverterASTTransformation extends GrulesASTTransformation {

  private final static String TMP_CLOSURE_VARIABLE_NAME = '__TMP_CLOSURE_VARIABLE'

  /**
   * Visits a converter method.
   */
  @Override
  void visit(ASTNode[] nodes, SourceUnit source) {
    MethodNode methodNode = (MethodNode) nodes[1]
    init(methodNode.name)
    visit(source.ast, methodNode)
  }

  /**
   * Mixins {@link org.grules.functions.ConverterBooleanResult} to a method return value to so the method can be used as
   * a converter.
   */
  @Override
  void visitModule(ModuleNode moduleNode, node) {
    MethodNode methodNode = node
    methodNode.parameters.each { Parameter parameter ->
      parameter.closureShare = true
    }
    BlockStatement methodBlockStatement = methodNode.code
    Expression closureExpression = GrulesASTFactory.createClosureExpression(methodNode.code)
    Expression arguments = new ArgumentListExpression([])
    Expression closureMethod = new ConstantExpression(GrulesASTFactory.CALL_METHOD_NAME)
    MethodCallExpression closureCall = new MethodCallExpression(closureExpression, closureMethod, arguments)
    closureCall.implicitThis = false
    Integer lineNumber = methodBlockStatement.lineNumber
    Integer columnNumber = methodBlockStatement.columnNumber
    Token assignmentOperator = new Token(Types.ASSIGN, Types.getText(Types.ASSIGN), lineNumber, columnNumber)
    Expression variableExpression = new VariableExpression(TMP_CLOSURE_VARIABLE_NAME)
    Expression booleanConstructorCallExpression = GrulesASTFactory.createConstructorCall(Boolean, [closureCall])
    // the constructor is called to prevent mix in on cached boolean value Boolean.TRUE or Boolean.FALSE
    Expression declarationExpression = new DeclarationExpression(variableExpression, assignmentOperator,
        booleanConstructorCallExpression)
    Expression metaClassProperty = new PropertyExpression(variableExpression, ExpandoMetaClass.META_CLASS_PROPERTY)
    Expression mixinMethodCall = GrulesASTFactory.createMethodCall(metaClassProperty,
        DefaultGroovyMethods.&mixin, [new ClassExpression(ClassHelper.make(ConverterBooleanResult))])
    Statement declarationStatement = new ExpressionStatement(declarationExpression)
    Statement mixinMethodCallStatement = new ExpressionStatement(mixinMethodCall)
    Statement returnExpression = new ExpressionStatement(variableExpression)
    methodBlockStatement.statements = [declarationStatement, mixinMethodCallStatement, returnExpression]
  }

}