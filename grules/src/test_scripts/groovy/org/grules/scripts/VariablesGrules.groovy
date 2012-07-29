package org.grules.scripts

import static org.grules.TestScriptEntities.*

binding.variables[VARIABLE_NAME] = VALID_INTEGER

"$PARAMETER_NAME" isEqual(binding.variables[VARIABLE_NAME])
