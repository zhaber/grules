package org.grules.script

import org.grules.GrulesInjector
import org.grules.ValidationErrorProperties

/**
 * Factory for rules scripts.
 */
class RulesScriptFactory {

  /**
   * Creates a main rules script that includes all others.
   *
   * @param scriptClass the rules script class
   * @param parameters input parameters
   * @return rules script
   */
  RulesScript newInstanceMain(Class<? extends Script> scriptClass, Map<String, Map<String, Object>> parameters,
        Map<String, Object> environment) {
    Script script = newScriptInstance(scriptClass, new RulesBinding())
    RulesScript rulesScript = script as RulesScript
    rulesScript.initMain(script, GrulesInjector.config, GrulesInjector.ruleEngine, parameters, environment)
    rulesScript
  }

  /**
   * Creates a script that is included into another rules script.
   *
   * @param script the rules script class
   * @param parentScripts scripts that included this script
   * @param missingRequiredParameters required but missing parameters
   * @param invalidParameters parameters that did not pass validation
   * @param parametersWithMissingDependency parameters that depend on a value of another missing parameter
   */
  protected RulesScript newInstanceIncluded(Class<? extends Script> rulesScriptClass,
      List<Class<? extends Script>> includedParentScripts, Binding binding,
      Map<String, Set<String>> missingRequiredParameters,
      Map<String, Map<String, ValidationErrorProperties>> invalidParameters,
      Map<String, Set<String>> parametersWithMissingDependency,
      Set<String> nologParameters) {
    Script script = newScriptInstance(rulesScriptClass, binding)
    RulesScript rulesScript = script as RulesScript
    rulesScript.initInclude(script, GrulesInjector.config, GrulesInjector.ruleEngine, includedParentScripts,
      missingRequiredParameters, invalidParameters, parametersWithMissingDependency, nologParameters)
    rulesScript
  }

  private Script newScriptInstance(Class<? extends Script> rulesScriptClass, Binding binding) {
    if (!Script.isAssignableFrom(rulesScriptClass)) {
      throw new IllegalArgumentException("$rulesScriptClass loaded by ${rulesScriptClass.classLoader} is not " +
          "assignable to Script loaded by ${Script.classLoader}. Make sure you use the same groovy library for " +
          'running the main application and jar dependencies.')
    }
    Script script = rulesScriptClass.newInstance()
    script.setBinding(binding)
    script.metaClass.mixin(RulesScript)
    script
  }
}