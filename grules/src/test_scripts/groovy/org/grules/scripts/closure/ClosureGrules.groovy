package org.grules.scripts.closure

import static org.grules.TestScriptEntities.*

"$PARAMETER_NAME" {it + 1} >> {it == VALID_INTEGER + 1} >> ~!{isPositiveInt(it)}
"$PARAMETER_NAME_AUX" {isInt(it)}