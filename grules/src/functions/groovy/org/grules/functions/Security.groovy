package org.grules.functions

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * A security validator checks for signs of client malicious intentions, which can be
 * usually determined from value syntax. To indicate that a certain function serves
 * this purpose, use the @Security annotation. For example:
 * <code>
 * @Security
 * boolean isSelect(String sqlValue) {
 *   sqlValue.contains(‘SELECT‘)
 * }
 * </code>
 * Actions that should be performed when a security validation fails can be defined in
 * the configuration file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD])
@interface Security {
}
