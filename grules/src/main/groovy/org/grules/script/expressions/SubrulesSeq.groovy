package org.grules.script.expressions

import org.grules.ValidationErrorProperties
import org.grules.ValidationException

/**
 * A sequence of subrules.
 */
class SubrulesSeq implements Term {

  private final List<Subrule> subrules = []
  private final List<String> skipFunctions = []

  /**
   * Applies subrules in order. If validation error is thrown and the source subrule does not have an error action,
   * it is taken from the first rule that comes after it and specifies non-empty error action, and if there is no such
   * subrule an empty error action is assumed.
   */
  @Override
  def apply(originalValue) {
    def value = originalValue
    for (int i = 0; i < subrules.size(); i++) {
      try {
        value = subrules[i].apply(value)
      } catch (ValidationException e) {
        Integer subruleWithValidaitonExceptionIndex = subrules.findIndexOf(i) {
          Subrule nextSubrule ->
          nextSubrule.errorProperties.hasAction()
        }
        if (subruleWithValidaitonExceptionIndex != -1) {
          e.addProperties(subrules[subruleWithValidaitonExceptionIndex].errorProperties)
        }
        ValidationErrorProperties errorProperties = e.errorProperties
        if (errorProperties.hasMessage()) {
          errorProperties.message = errorProperties.message.replaceAll('_', originalValue)
        }
        errorProperties.value = originalValue
        errorProperties.subruleIndex = i + 1
        throw e
      }
    }
    value
  }

  /**
   * Adds the given subrule to the subrules sequence.
   */
  SubrulesSeq add(Subrule subrule) {
    subrules << subrule
    this
  }

  /**
   * Wraps the given rule expression into a subrule and adds it to the subrules sequence.
   */
  SubrulesSeq add(expression) {
    subrules << SubruleFactory.create(expression)
    this
  }

  void addSkipFunction(String functionName) {
    skipFunctions << functionName
  }

  boolean hasSkipFunction(Subrule functionSubrule) {
    (functionSubrule.term instanceof FunctionTerm) && (functionSubrule.term as FunctionTerm).name in skipFunctions
  }

  @Override
  String toString() {
    "Subrules sequence: $subrules"
  }
}
