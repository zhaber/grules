package org.grules.script;
import groovy.lang.Closure;
import groovy.lang.Script;

import java.util.Map;
import java.util.Set;

import org.grules.ValidationErrorProperties;

import org.grules.script.expressions.Skip;
import org.grules.script.expressions.SubrulesSeq;

public interface RulesScriptAPI {
  void include(Class<? extends Script> includedScriptClass);
  void validate(Closure<Map<String, Object>> closure);
  void changeGroup(String group);
  void addParameter(String name, Object value);
  void applyRuleToRequiredParameter(String name, Closure<SubrulesSeq> subrulesSeqClosure);
  void applyRuleToOptionalParameter(String name, Closure<SubrulesSeq> subrulesSeqClosure, Object defaultValue);
  void applyRuleToParametersList(String ruleName, Set<String> requiredParameters,
      Map<String, Object> optionalParameters, Closure<SubrulesSeq> subrulesSeqClosure);
  Skip skip(String... converters);
  ValidationErrorProperties e(Map<String, Object> properties);
  ValidationErrorProperties e(String errorMessage, Map<String, Object> properties);
  ValidationErrorProperties e(Map<String, Object> properties, String errorMessage);
  ValidationErrorProperties e(String errorMessage);
  void nolog(String... parameters);
}