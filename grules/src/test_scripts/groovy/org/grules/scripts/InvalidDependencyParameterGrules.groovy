package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.TestScriptEntities

"$PARAMETER_NAME" toInt
"$PARAMETER_NAME_AUX" eq(this."$TestScriptEntities.PARAMETER_NAME")
