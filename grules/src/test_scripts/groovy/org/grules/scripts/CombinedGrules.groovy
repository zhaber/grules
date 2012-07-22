package org.grules.scripts

import static org.grules.TestScriptEntities.*

id toPositiveInt >> !isEven && isOdd

closure {it == PARAMETER_VALUE}

withDefaultValue[PARAMETER_VALUE] eq($closure)

equalToWithDefaultValue eq(withDefaultValue)

invalidParameter isEmpty
