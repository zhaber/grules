package org.grules.scripts

import static org.grules.TestScriptEntities.*

"$PARAMETER_NAME" toInt >> !(isEven && isOdd)
"$PARAMETER_NAME_AUX" toInt >> isEven || !isOdd [ERROR_MSG] 