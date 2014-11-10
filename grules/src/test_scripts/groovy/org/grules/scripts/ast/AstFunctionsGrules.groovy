package org.grules.scripts.ast

import static org.grules.TestScriptEntities.PARAMETER_NAME
import static org.grules.TestScriptEntities.PARAMETER_NAME_AUX

"$PARAMETER_NAME" ~ { nop(it) }
"$PARAMETER_NAME_AUX" nop
