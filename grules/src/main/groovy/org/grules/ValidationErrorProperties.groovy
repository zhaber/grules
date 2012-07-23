package org.grules

class ValidationErrorProperties {
  final static String INPUT_ELEMENT = 'element'
  final static String REDIRECT_URL = 'url'
  final static String MESSAGE = 'msg'
  final static String VALUE = 'value'
  final static String SUBRULE_INDEX = 'subruleIndex'
  final static String FUNCTION_NAME = 'functionName'

  private final Map<String, Object> errorProperties

  ValidationErrorProperties() {
    this.errorProperties = [:]
  }

  ValidationErrorProperties(Map<String, Object> errorProperties) {
    this.errorProperties = errorProperties
  }

  ValidationErrorProperties(String message, Map<String, Object> errorProperties) {
    this.errorProperties = [(MESSAGE): message] + errorProperties
  }

  ValidationErrorProperties(String message) {
    this.errorProperties = [(MESSAGE): message]
  }

  void merge(ValidationErrorProperties errorerrorProperties) {
    errorProperties.putAll(errorerrorProperties.errorProperties)
  }

  @Override
  String toString() {
    errorProperties.toString()
  }

  Object getErrorProperty(String errorProperty) {
    errorProperties[errorProperty]
  }

  boolean hasErrorProperty(String errorProperty) {
    errorProperties.containsKey(errorProperty)
  }

  /**
   * An error message string.
   */
  String getMessage() {
    errorProperties[MESSAGE]
  }

  /**
   * Sets an error message string.
   */
  void setMessage(String message) {
    errorProperties[MESSAGE] = message
  }

  /**
   * A redirect URL that has to used on validation error event.
   */
  String getRedirectUrl() {
    errorProperties[ValidationErrorProperties.REDIRECT_URL]
  }

  /**
   * Checks if the validation error specifies an input element that has to used on validation error event.
   */
  boolean hasInputElement() {
    errorProperties.containsKey(INPUT_ELEMENT)
  }

  /**
   * Checks if the validation error specifies an error message that has to be shown on validation error event.
   */
  boolean hasMessage() {
    errorProperties.containsKey(MESSAGE)
  }

  /**
   * Checks if the validation error specifies a redirect URL that has to used on validation error event.
   */
  boolean hasRedirectUrl() {
    errorProperties.containsKey(REDIRECT_URL)
  }

  /**
   * A redirect URL that has to used on validation error event.
   */
  String getInputElement() {
    errorProperties[INPUT_ELEMENT]
  }

  /**
   * Checks if the validation error specifies a property value.
   */
  boolean hasValue() {
    errorProperties.containsKey(VALUE)
  }

  /**
   * Value of an invalid property.
   */
  String getValue() {
    errorProperties[VALUE]
  }

  /**
   * Sets value of an invalid property.
   */
  void setValue(value) {
    errorProperties[VALUE] = value
  }

  /**
   * An index of a failed subrule.
   */
  Integer getSubruleIndex() {
    errorProperties[SUBRULE_INDEX]
  }

  /**
   * Sets an index of a failed subrule.
   */
  void setSubruleIndex(Integer subruleIndex) {
    errorProperties[SUBRULE_INDEX] = subruleIndex
  }

  /**
   * Checks if the validation error specifies any action that should be takes on validation error event.
   */
  boolean hasAction() {
    hasRedirectUrl() || hasMessage()
  }

}