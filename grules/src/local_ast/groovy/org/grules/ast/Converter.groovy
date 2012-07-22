package org.grules.ast

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

import org.codehaus.groovy.transform.GroovyASTTransformationClass

/**
 * Indicates that the function is a converter. Can be used for functions that return a boolean value.
 */
@java.lang.annotation.Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@GroovyASTTransformationClass('org.grules.ast.ConverterASTTransformation')
@interface Converter {
}
