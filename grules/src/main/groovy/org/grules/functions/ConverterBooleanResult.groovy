package org.grules.functions

/**
 * A mixed in class for a converter function return value used to distinguish between validators and boolean converters.
 *
 * It should be a class and not an interface because Groovy does not allow to mixin interfaces, and we can't create
 * proxy or wrapper because we change signature should be such that returned value is Boolean
 */
class ConverterBooleanResult {
}

