package org.grules.ast

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.objectweb.asm.Opcodes

/**
 * Transformation for a functions class. The transformation makes all instance methods static.
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class FunctionsASTTransformation extends GrulesAstTransformation {

  @Override
  void visit(ASTNode[] nodes, SourceUnit source) {
    ClassNode classNode = nodes[1]
    init(classNode.name)
    visit(source.ast, classNode)
  }

  /**
   * Visits each method and makes it static.
   */
  @Override
  void visitModule(ModuleNode moduleNode, node) {
    ClassNode classNode = node
    classNode.methods.each {
      MethodNode methodNode ->
      methodNode.modifiers = methodNode.modifiers | Opcodes.ACC_STATIC
      methodNode.setParameters(methodNode.parameters) //set static context to parameters
      (methodNode.code as BlockStatement).scope.parent = methodNode.variableScope
    }
  }

}
