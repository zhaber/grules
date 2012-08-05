package org.grules.scripts

import static org.grules.TestScriptEntities.*


"$PARAMETER_NAME" toInt >> (it == VALID_INTEGER ? {FUNCTION_FOR_ONE_ARGUMENT(it)} : isFalse)