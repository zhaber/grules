package org.grules.scripts

import static org.grules.TestScriptEntities.*


rules {
  isId = {it * 10} >> {it + 1} >> isPositiveInt & isOdd
}

"$PARAMETER_NAME" isPositiveInt
"$PARAMETER_NAME_AUX" isId >> nop