package org.grules

import org.grules.script.RulesBinding

class EmptyRulesScript extends Script {
		
	EmptyRulesScript() {
		super(new RulesBinding()) 
	}
	
	@Override
	def run() {
	} 
}