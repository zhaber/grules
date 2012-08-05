package org.grules.script

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
    Script script = newScriptInstance(scriptClass)
    RulesScript rulesScript = script as RulesScript
    rulesScript.initMain(script, parameters, environment)
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
  protected RulesScript newInstanceIncluded(Class<? extends Script> scriptClass,
      List<Class<? extends Script>> includedParentScripts, Binding binding,
      Map<String, Set<String>> missingRequiredParameters,
      Map<String, Map<String, ValidationErrorProperties>> invalidParameters,
      Map<String, Set<String>> parametersWithMissingDependency,
      Set<String> nologParameters) {
    Script script = newScriptInstance(scriptClass, binding)
    RulesScript rulesScript = script as RulesScript
    rulesScript.initInclude(script, includedParentScripts, missingRequiredParameters, invalidParameters,
        parametersWithMissingDependency, nologParameters)
    rulesScript
  }

  private Script newScriptInstance(Class<? extends Script> scriptClass, Binding binding = new RulesBinding()) {
    if (!Script.isAssignableFrom(scriptClass)) {
      throw new IllegalArgumentException("$scriptClass is not of Script class")
    }
    Script script = scriptClass.newInstance()
    script.setBinding(binding)
    script.metaClass.mixin(RulesScript)
    script
  }
}