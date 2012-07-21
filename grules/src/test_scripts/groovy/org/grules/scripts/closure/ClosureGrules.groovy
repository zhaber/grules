package org.grules.scripts.closure

import static org.grules.TestScriptEntities.*

"$PARAMETER_NAME" {it + 1} >> {it == VALID_INTEGER + 1} >> ~!{isPositive(it)}
"$PARAMETER_NAME_AUX" {toInt(it)}