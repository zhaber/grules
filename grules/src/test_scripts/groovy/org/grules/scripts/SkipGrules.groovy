package org.grules.scripts

import static org.grules.TestScriptEntities.*

import org.grules.script.expressions.FunctionTerm


"$PARAMETER_NAME" skip((CONFIG.defaultFunctions[0].term as FunctionTerm).name)