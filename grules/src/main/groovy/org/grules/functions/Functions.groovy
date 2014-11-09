package org.grules.functions

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

import org.codehaus.groovy.transform.GroovyASTTransformationClass

/**
 * Transforms an instance-style Groovy class to become a static-style class.
 */
@java.lang.annotation.Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@GroovyASTTransformationClass('org.grules.ast.FunctionsASTTransformation')
@interface Functions {
}

