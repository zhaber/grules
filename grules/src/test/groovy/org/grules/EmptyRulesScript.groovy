package org.grules

import org.grules.script.RulesBinding

/**
 * Script without any rules.
 */
class EmptyRulesScript extends Script {

  EmptyRulesScript() {
    super(new RulesBinding())
  }

  @Override
  def run() {
  }
}
