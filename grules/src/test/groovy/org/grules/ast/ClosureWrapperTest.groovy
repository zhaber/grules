package org.grules.ast

import static org.grules.ast.ASTTestUtils.fetchArguments
import static org.grules.ast.ASTTestUtils.fetchStatementBlockExpression
import static org.grules.ast.ASTTestUtils.fetchClosureExpression

import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.control.CompilePhase

import spock.lang.Specification

class ClosureWrapperTest extends Specification {

  private AstBuilder builder
  private CompilePhase phase

  private final a = 'a'
  private final b = 'b'

  def setup() {
    builder = new AstBuilder()
    phase = CompilePhase.CONVERSION
  }

  def "wrapInClosures for method"() {
    def ruleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
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
      def ruleExpression = fetchStatementBlockExpression(builder.buildFromCode(phase) {
        ~ { a }
      })
      ruleExpression = RuleExpressionFormTransformer.convertPrecedences(ruleExpression)
      ruleExpression = RulesAstTransformation.liftErrors(ruleExpression)
      ruleExpression = ClosureWrapper.wrapInClosures(ruleExpression)
    expect:
      ruleExpression instanceof BitwiseNegationExpression
      (ruleExpression as BitwiseNegationExpression).expression instanceof ClosureExpression
  }
}
