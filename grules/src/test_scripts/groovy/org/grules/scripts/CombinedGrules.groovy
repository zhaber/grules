package org.grules.scripts

import static org.grules.TestScriptEntities.PARAMETER_VALUE

id toPositiveInt >> !isEven && isOdd

closure { it == PARAMETER_VALUE }

withDefaultValue[PARAMETER_VALUE] isEqual($closure)

equalToWithDefaultValue eq(withDefaultValue)

invalidParameter isEmpty
