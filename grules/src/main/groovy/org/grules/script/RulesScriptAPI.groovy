package org.grules.script

import org.grules.script.expressions.SubrulesSeq

interface RulesScriptAPI {
  void include(Class<? extends Script> includedScriptClass)
  void validate(Closure<Map<String, Object>> closure)
  void rules(Closure<Void> closure)
  void changeGroup(String group)
  void applyRuleToRequiredParameter(String name, Closure<SubrulesSeq> subrulesSeqClosure)
  void applyRuleToOptionalParameter(String name, Closure<SubrulesSeq> subrulesSeqClosure, defaultValue)
  void applyRuleToParametersGroup(List<String> names, String name, Closure<SubrulesSeq> subrulesSeqClosure)
  SubrulesSeq skip(String... converters)
}