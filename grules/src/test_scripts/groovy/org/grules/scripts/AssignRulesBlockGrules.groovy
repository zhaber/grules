package org.grules.scripts

import static org.grules.TestScriptEntities.*

rules {
  isId = mult(10) >> {it + 1} >> isPositiveInt & isOdd
}

"$PARAMETER_NAME" isId
"$PARAMETER_NAME_AUX" isId >> nop