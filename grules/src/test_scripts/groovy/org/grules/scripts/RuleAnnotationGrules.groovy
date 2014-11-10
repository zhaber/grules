package org.grules.scripts

import static org.grules.TestScriptEntities.FUNCTION_FOR_TWO_ARGUMENTS
import static org.grules.TestScriptEntities.VALID_INTEGER
import static org.grules.TestScriptEntities.PARAMETER_NAME
import static org.grules.TestScriptEntities.PARAMETER_NAME_AUX

import org.grules.script.Rule

@Rule
rule = { FUNCTION_FOR_TWO_ARGUMENTS(VALID_INTEGER) }
@Rule
ruleAux = { parameter, number -> { value -> FUNCTION_FOR_TWO_ARGUMENTS(value, number) } }

"$PARAMETER_NAME" rule
"$PARAMETER_NAME_AUX" ruleAux(VALID_INTEGER)
