package org.grules.script.expressions

import org.grules.GrulesException

/**
 * Signals an invalid subrules sequence.
 */
class InvalidSubrulesSeqException extends GrulesException {
	
	InvalidSubrulesSeqException(SubrulesSeq subrulesSeq, Subrule subrule) {
		super("Subrules sequence '$subrulesSeq >> $subrule' is invalid")
	}
	
	InvalidSubrulesSeqException(subrulesSeq) {
		super("Subrules sequence '$subrulesSeq' of type ${subrulesSeq?.class} is invalid")
	}
}