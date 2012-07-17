package org.grules.ast

import groovy.inspect.swingui.AstNodeToScriptVisitor

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
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
	
	void visit(ModuleNode moduleNode, node) {
		try {
		  visitModule(moduleNode, node)
		  AstNodeToScriptVisitor astNodeToScriptVisitor = new AstNodeToScriptVisitor(logger.writer)
			switch (node.class) {
				case ClassNode: astNodeToScriptVisitor.visitClass(node)
				                 break
    		case MethodNode: astNodeToScriptVisitor.visitMethod(node)
												 break
			}
			
		} catch (Throwable exception) {
			StringWriter stringWriter = new StringWriter()
		  Throwable sanitizedException = StackTraceUtils.deepSanitize(exception)
		  sanitizedException.printStackTrace(new PrintWriter(stringWriter))
      log(stringWriter)	
		} finally {
		  close()
		}
	}
	
	abstract void visitModule(ModuleNode moduleNode, node) 
	
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