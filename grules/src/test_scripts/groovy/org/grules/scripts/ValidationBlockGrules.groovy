package org.grules.scripts

import static org.grules.TestScriptEntities.PARAMETER_NAME
import static org.grules.TestScriptEntities.PARAMETER_VALUE
import static org.grules.TestScriptEntities.ERROR_ID
import static org.grules.TestScriptEntities.ERROR_MESSAGE
import static org.grules.TestScriptEntities.PARAMETER_NAME_AUX

import org.grules.ValidationException

validate {
  [(PARAMETER_NAME):PARAMETER_VALUE]
}

validate {
  throw new ValidationException(ERROR_ID, ERROR_MESSAGE, PARAMETER_NAME_AUX)
}
