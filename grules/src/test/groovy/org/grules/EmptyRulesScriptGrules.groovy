package org.grules

import org.grules.script.RulesBinding

/**
 * Script without any rules.
 */
class EmptyRulesScriptGrules extends Script {

  EmptyRulesScriptGrules() {
    super(new RulesBinding())
  }

  @Override
  def run() {
  }
}
