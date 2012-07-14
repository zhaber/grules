package org.grules.ast

import groovy.inspect.swingui.AstNodeToScriptVisitor

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.runtime.StackTraceUtils
import org.codehaus.groovy.transform.ASTTransformation

/**
 * The class is common ancestor for grules script transformations.
 */
abstract class GrulesASTTransformation implements ASTTransformation {

	private GrulesASTTransformationLogger logger
	
	protected void init(String className) {
	  logger = new GrulesASTTransformationLogger((className.split('\\.') as List).last())
	}
	
	void visit(ModuleNode moduleNode, ClassNode classNode) {
		try {
		  visitModule(moduleNode, classNode)
		  AstNodeToScriptVisitor astNodeToScriptVisitor = new AstNodeToScriptVisitor(logger.writer)
			astNodeToScriptVisitor.visitClass(classNode)
		} catch (Throwable exception) {
			StringWriter stringWriter = new StringWriter()
		  Throwable sanitizedException = StackTraceUtils.deepSanitize(exception)
		  sanitizedException.printStackTrace(new PrintWriter(stringWriter))
      log(stringWriter)	
		} finally {
		  close()
		}
	}
	
	abstract void visitModule(ModuleNode moduleNode, ClassNode classNode) 
	
	void log(message) {
		logger.write(message.toString())
	}
	
	void log(String label, message) {
		log(label + ': ' + message)
	}
	
	private void close() {
		log('\nTransformation complete')
		logger.close()
	}
}