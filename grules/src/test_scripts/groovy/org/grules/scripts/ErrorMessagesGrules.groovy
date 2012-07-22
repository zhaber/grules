package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.ValidationErrorProperties

"$PARAMETER_NAME" eq(PARAMETER_NAME_AUX) || isEmpty [e((ValidationErrorProperties.MESSAGE_ERROR_PROPERTY): ERROR_MSG)]
"$PARAMETER_NAME_AUX" contains(PARAMETER_VALUE) && eq(PARAMETER_NAME) >> nop [m[ERROR_MSG]]
