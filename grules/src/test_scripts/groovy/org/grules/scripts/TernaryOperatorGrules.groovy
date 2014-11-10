package org.grules.scripts

import static org.grules.TestScriptEntities.PARAMETER_NAME
import static org.grules.TestScriptEntities.VALID_INTEGER
import static org.grules.TestScriptEntities.FUNCTION_FOR_ONE_ARGUMENT

"$PARAMETER_NAME" toInt >> (it == VALID_INTEGER ? { FUNCTION_FOR_ONE_ARGUMENT(it) } : isFalse)
