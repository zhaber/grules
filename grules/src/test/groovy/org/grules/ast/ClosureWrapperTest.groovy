package org.grules.ast
import static org.grules.TestScriptEntities.*
import static org.grules.ast.ASTTestUtils.*

import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.control.CompilePhase

import spock.lang.Specification

class ClosureWrapperTest extends Specification {
	
	AstBuilder builder
	CompilePhase phase
	
	def a = 'a'
	def b = 'b'
	
	def setup() {
		builder = new AstBuilder()
		phase = CompilePhase.CONVERSION
	}
	
	def "wrapInClosures for method"() {
		def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
			a(b)
		})
		ruleExpression = ClosureWrapper.wrapInClosures(ruleExpression)
	expect:
		ruleExpression instanceof ConstructorCallExpression
		fetchArguments(ruleExpression).size == 2
		fetchArguments(ruleExpression)[0] instanceof ClosureExpression
		fetchClosureExpression(fetchArguments(ruleExpression)[0]) instanceof MethodCallExpression
		fetchArguments(fetchClosureExpression(fetchArguments(ruleExpression)[0])).size == 2
	}
	
	def "wrapInClosures for bitwise negation expression"() {
		setup:
			def ruleExpression = fetchStatementBlocksExpression(builder.buildFromCode(phase) {
				~{a}
			})
			ruleExpression = convertPrecedences(ruleExpression)
			ruleExpression = RulesASTTransformation.liftErrors(ruleExpression)
			ruleExpression = ClosureWrapper.wrapInClosures(ruleExpression)
		expect:
			ruleExpression instanceof BitwiseNegationExpression
			(ruleExpression as BitwiseNegationExpression).expression instanceof ClosureExpression
	}
}