package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.ValidationErrorProperties

"$PARAMETER_NAME" eq(PARAMETER_NAME_AUX) || isEmpty [e((ValidationErrorProperties.ERROR_ID): ERROR_ID)]
"$PARAMETER_NAME_AUX" contains(PARAMETER_VALUE) && isEqual(PARAMETER_NAME) >> nop [m[ERROR_ID]]
