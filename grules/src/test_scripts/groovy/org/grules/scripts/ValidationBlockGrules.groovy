package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.ValidationException


validate {
  [(PARAMETER_NAME): PARAMETER_VALUE]
}

validate {
  throw new ValidationException(ERROR_ID, ERROR_MESSAGE, PARAMETER_NAME_AUX)
}