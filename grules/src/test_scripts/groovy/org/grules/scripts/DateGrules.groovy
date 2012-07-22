package org.grules.scripts

import static org.grules.TestScriptEntities.*

"$PARAMETER_NAME" toDate(DATE_FORMAT) >> isAfterNow
"$PARAMETER_NAME_AUX" isBeforeNow
