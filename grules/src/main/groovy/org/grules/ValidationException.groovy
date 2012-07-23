package org.grules

/**
 * Signals a validation error.
 */
class ValidationException extends GrulesException {

  final ValidationErrorProperties errorProperties

  ValidationException() {
    this.errorProperties = new ValidationErrorProperties()
  }

  ValidationException(String message) {
    this.errorProperties = new ValidationErrorProperties(message)
  }

  ValidationException(Map<String, Object> errorProperties) {
    this.errorProperties = new ValidationErrorProperties(errorProperties)
  }

  void addProperties(ValidationErrorProperties properties) {
    errorProperties.merge(properties)
  }

}
