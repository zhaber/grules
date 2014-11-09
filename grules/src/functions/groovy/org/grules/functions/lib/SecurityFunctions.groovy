package org.grules.functions.lib;import org.grules.functions.Functions

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
@Functions
class SecurityFunctions {
}
