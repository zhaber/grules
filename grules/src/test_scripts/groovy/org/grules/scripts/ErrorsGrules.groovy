package org.grules.scripts

import static org.grules.TestScriptEntities.PARAMETER_NAME_AUX
import static org.grules.TestScriptEntities.PARAMETER_NAME
import static org.grules.TestScriptEntities.ERROR_ID
import static org.grules.TestScriptEntities.PARAMETER_VALUE

import org.grules.ValidationErrorProperties

"$PARAMETER_NAME" eq(PARAMETER_NAME_AUX) || isEmpty [e((ValidationErrorProperties.ERROR_ID):ERROR_ID)]
"$PARAMETER_NAME_AUX" contains(PARAMETER_VALUE) && isEqual(PARAMETER_NAME) >> nop [m[ERROR_ID]]
