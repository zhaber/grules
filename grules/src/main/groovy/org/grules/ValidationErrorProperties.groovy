package org.grules

class ValidationErrorProperties {
  final static String INPUT_ELEMENT_ERROR_PROPERTY = 'element'
  final static String REDIRECT_URL_ERROR_PROPERTY = 'url'
  final static String MESSAGE_ERROR_PROPERTY = 'msg'
  final static String VALUE_ERROR_PROPERTY = 'value'
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
    this.errorProperties = [(MESSAGE_ERROR_PROPERTY): message] + errorProperties
  }

  ValidationErrorProperties(String message) {
    this.errorProperties = [(MESSAGE_ERROR_PROPERTY): message]
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
   * An error message that has to be shown on validation error event.
   */
  String getMessage() {
    errorProperties[MESSAGE_ERROR_PROPERTY]
  }

  /**
   * A redirect URL that has to used on validation error event.
   */
  String getRedirectUrl() {
    errorProperties[ValidationErrorProperties.REDIRECT_URL_ERROR_PROPERTY]
  }

  /**
   * Checks if the validation error specifies an input element that has to used on validation error event.
   */
  boolean hasInputElement() {
    errorProperties.containsKey(INPUT_ELEMENT_ERROR_PROPERTY)
  }

  /**
   * Checks if the validation error specifies an error message that has to be shown on validation error event.
   */
  boolean hasMessage() {
    errorProperties.containsKey(MESSAGE_ERROR_PROPERTY)
  }

  /**
   * Checks if the validation error specifies a redirect URL that has to used on validation error event.
   */
  boolean hasRedirectUrl() {
    errorProperties.containsKey(REDIRECT_URL_ERROR_PROPERTY)
  }

  /**
   * A redirect URL that has to used on validation error event.
   */
  String getInputElement() {
    errorProperties[INPUT_ELEMENT_ERROR_PROPERTY]
  }

  /**
   * Checks if the validation error specifies a property value.
   */
  boolean hasValue() {
    errorProperties.containsKey(VALUE_ERROR_PROPERTY)
  }

  /**
   * Value of an invalid property.
   */
  String getValue() {
    errorProperties[VALUE_ERROR_PROPERTY]
  }

  /**
   * Sets value of an invalid property.
   */
  void setValue(value) {
    errorProperties[VALUE_ERROR_PROPERTY] = value
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
