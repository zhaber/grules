package org.grules.scripts

import static org.grules.TestScriptEntities.VARIABLE_NAME
import static org.grules.TestScriptEntities.VALID_INTEGER
import static org.grules.TestScriptEntities.PARAMETER_NAME

binding.variables[VARIABLE_NAME] = VALID_INTEGER

"$PARAMETER_NAME" isEqual(binding.variables[VARIABLE_NAME])
