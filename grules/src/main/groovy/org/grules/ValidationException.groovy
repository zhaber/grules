package org.grules

/**
 * Signals a validation error.
 */
class ValidationException extends GrulesException {

  final ValidationErrorProperties errorProperties

  ValidationException() {
    this.errorProperties = new ValidationErrorProperties()
  }

  ValidationException(errorId) {
    this.errorProperties = new ValidationErrorProperties(errorId)
  }

  ValidationException(errorId, String message) {
    this.errorProperties = new ValidationErrorProperties(errorId, message)
  }

  ValidationException(errorId, String message, String parameter) {
    this.errorProperties = new ValidationErrorProperties(errorId, message, parameter)
  }

  ValidationException(Map<String, Object> errorProperties) {
    this.errorProperties = new ValidationErrorProperties(errorProperties)
  }

  void addProperties(ValidationErrorProperties errorProperties) {
    this.errorProperties.merge(errorProperties)
  }
}

