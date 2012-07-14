package org.grules.ast

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.objectweb.asm.Opcodes

/**
 * Transformations of an abstract syntax tree for functions scripts. The transformation makes instance methods static 
 * and adds a new static method for a shortcut call.
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class FunctionsASTTransformation extends GrulesASTTransformation {
	
	/**
	 * Apply transformations to a functions class.
	 * 
	 * @param moduleNode module with functions
	 */
	@Override
	void visit(ASTNode[] nodes, SourceUnit source) {
		ClassNode classNode = nodes[1]
		init(classNode.name)
		visit(source.ast, classNode)
	}
	
	@Override
	void visitModule(ModuleNode moduleNode, ClassNode classNode) {
		classNode.methods.each {
			MethodNode methodNode ->
			methodNode.modifiers = methodNode.modifiers | Opcodes.ACC_STATIC
			methodNode.setParameters(methodNode.parameters) //set static context to parameters
		}
	}
	
}
