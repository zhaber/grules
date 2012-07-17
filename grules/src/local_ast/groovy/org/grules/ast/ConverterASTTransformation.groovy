package org.grules.ast

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.VariableScope
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.grules.GroovyConstants
import org.grules.functions.ConverterBooleanResult

/**
 * Transformation for a converter function. 
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class ConverterASTTransformation extends GrulesASTTransformation {
	
	@Override
	void visit(ASTNode[] nodes, SourceUnit source) {
    MethodNode methodNode = (MethodNode) nodes[1]		
		init(methodNode.name)
		visit(source.ast, methodNode)
	}
	
	@Override
	void visitModule(ModuleNode moduleNode, node) {
		MethodNode methodNode = node 
		methodNode.parameters.each { Parameter parameter ->
			parameter.closureShare = true
		}
    methodNode.returnType = ClassHelper.make(Object) 
		BlockStatement methodBlockStatement = methodNode.code
		VariableScope closureVariableScope = methodBlockStatement.scope.copy()
		closureVariableScope.parent = methodBlockStatement.scope
		List<Statement> statements = methodBlockStatement.statements
		VariableScope closureBlockVariableScope = closureVariableScope.copy()
		closureBlockVariableScope.parent = closureVariableScope
		BlockStatement closureBlockStatement = new BlockStatement(statements, closureBlockVariableScope)
    ClosureExpression closure = new ClosureExpression(new Parameter[0], closureBlockStatement)
		closure.setVariableScope(closureVariableScope)
		ArgumentListExpression arguments = new ArgumentListExpression([])
		ConstantExpression closureMethod = new ConstantExpression(GroovyConstants.CALL_METHOD_NAME)
		MethodCallExpression closureCall = new MethodCallExpression(closure, closureMethod, arguments)
		closureCall.implicitThis = false
		MethodCallExpression wrapperMethodCall = GrulesASTFactory.createStaticMethodCall(ConverterBooleanResult, 
			  ConverterBooleanResult.&wrap, [closureCall])
		methodBlockStatement.statements = [new ExpressionStatement(wrapperMethodCall)]
	}
	
}
