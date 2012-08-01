package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.script.Rule

@Rule
rule = FUNCTION_FOR_TWO_ARGUMENTS(VALID_INTEGER)
@Rule
ruleAux = {parameter, number -> {value -> FUNCTION_FOR_TWO_ARGUMENTS(value, number)}}

"$PARAMETER_NAME" rule
"$PARAMETER_NAME_AUX" ruleAux(VALID_INTEGER)