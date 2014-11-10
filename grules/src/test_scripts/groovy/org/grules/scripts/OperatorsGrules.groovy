package org.grules.scripts

import static org.grules.TestScriptEntities.PARAMETER_NAME
import static org.grules.TestScriptEntities.ERROR_ID
import static org.grules.TestScriptEntities.PARAMETER_NAME_AUX

"$PARAMETER_NAME" toInt >> !(isEven && isOdd)
"$PARAMETER_NAME_AUX" toInt >> isEven || !isOdd [ERROR_ID]
