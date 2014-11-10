package org.grules.scripts.closure

import static org.grules.TestScriptEntities.PARAMETER_NAME
import static org.grules.TestScriptEntities.VALID_INTEGER
import static org.grules.TestScriptEntities.PARAMETER_NAME_AUX

"$PARAMETER_NAME" { it + 1 } >> { it == VALID_INTEGER + 1 } >> ~! { isPositive(it) }
"$PARAMETER_NAME_AUX" { toInt(it) }
