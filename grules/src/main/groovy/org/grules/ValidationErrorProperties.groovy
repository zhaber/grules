package org.grules

/**
 * Validation error properties.
 */
class ValidationErrorProperties {
  final static String PARAMETER = 'parameter'
  final static String INPUT_ELEMENT = 'element'
  final static String REDIRECT_URL = 'url'
  final static String ERROR_ID = 'errorId'
  final static String MESSAGE = 'message'
  final static String VALUE = 'value'
  final static String SUBRULE_INDEX = 'subruleIndex'
  final static String EXCEPTION = 'exception'

  private final Map<String, Object> errorProperties

  ValidationErrorProperties() {
    this([:])
  }

  ValidationErrorProperties(Map<String, Object> errorProperties) {
    this.errorProperties = errorProperties.collectEntries { String key, value -> value != null ? [(key):value] : [:] }
  }

  ValidationErrorProperties(errorId, Map<String, Object> errorProperties) {
    this([(ERROR_ID):errorId] + errorProperties)
  }

  ValidationErrorProperties(errorId, String message, Map<String, Object> errorProperties) {
    this(errorId, [(MESSAGE):message] + errorProperties)
  }

  ValidationErrorProperties(errorId, String message, String parameter, Map<String, Object> errorProperties) {
    this(errorId, message, [(PARAMETER):parameter] + errorProperties)
  }

  ValidationErrorProperties(errorId) {
    this(errorId, [:])
  }

  ValidationErrorProperties(errorId, String message) {
    this(errorId, message, [:])
  }

  ValidationErrorProperties(errorId, String message, String parameter) {
    this(errorId, message, parameter, [:])
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
   * An exception that causes the error.
   */
  Exception getException() {
    errorProperties[EXCEPTION]
  }

  /**
   * Sets an error message string.
   */
  void setMessage(String message) {
    errorProperties[MESSAGE] = message
  }

  /**
   * An error code.
   */
  def getErrorId() {
    errorProperties[ERROR_ID]
  }

  /**
   * Sets an error id. It can be any object that identifies the error.
   */
  void setErrorId(errorId) {
    errorProperties[ERROR_ID] = errorId
  }

  /**
   * Sets an exception.
   */
  void setException(Exception exception) {
    errorProperties[EXCEPTION] = exception
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
   * Checks if the validation error specifies an error id.
   */
  boolean hasErrorId() {
    errorProperties.containsKey(ERROR_ID)
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
   * A name of the parameter that did not pass validation.
   */
  String getParameter() {
    errorProperties[PARAMETER]
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
   * An index of a failed subrule. Indexes start from 1. Negative index means that a default function with this index
   * failed.
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
    hasRedirectUrl() || hasErrorId()
  }

}

