package org.grules.script

import groovyx.gpars.GParsExecutorsPool
import groovyx.gpars.GParsExecutorsPoolUtil

import java.util.concurrent.ExecutionException

import org.grules.GrulesLogger
import org.grules.ValidationErrorProperties
import org.grules.config.ConfigException
import org.grules.config.GrulesConfig
import org.grules.config.OnValidationEventAction
import org.grules.script.expressions.Operators

import com.google.inject.Inject

/**
 * The RuleEngine class manages execution of rules scripts.
 */
class RuleEngine {

  private final GrulesConfig config
  private final RulesScriptFactory rulesScriptFactory
  public static final String RULES_FILE_SUFFIX = 'Grules'

  @Inject
  RuleEngine(GrulesConfig config, RulesScriptFactory rulesScriptFactory) {
    this.config = config
    this.rulesScriptFactory = rulesScriptFactory
  }

  /**
   * Creates a closure that applies a rules script to given parameters values. Parameters is a map where keys are
   * parameters names and values are parameters values (<code>Map&lt;String, Object></code>)
   */
  Closure<RulesScriptResult> newExecutor(Class<? extends Script> rulesScriptClass, Map<String, Object> environment) {
    String defaultGroup = config.getDefaultGroup()
    return { Map<String, Object> parameters ->
      RulesScript script = runMainScript(rulesScriptClass, [(defaultGroup):parameters], environment)
      RulesScriptResult scriptResult = RulesScriptResultFetcher.fetchResult(script, defaultGroup, parameters)
      checkMissingFlatParameters(scriptResult.notValidatedParameters)
      scriptResult
    }
  }

  /**
   * Creates a closure that applies a rules script to given parameters values. Parameters is a map where keys are
   * parameters groups and values are maps of the first type (<code>Map&lt;ParametersGroup, Map&lt;String, Object>>
   * </code>)
   */
  Closure<RulesScriptGroupResult> newGroupExecutor(Class<? extends Script> rulesScriptClass,
      Map<String, Object> environment) {
    return { Map<String, Map<String, Object>> parameters ->
      parameters.each { String group, groupParameters ->
        if (!config.getGroups().contains(group)) {
          throw new InvalidGroupException(group)
        }
      }
      RulesScript script = runMainScript(rulesScriptClass, parameters, environment)
      RulesScriptGroupResult scriptResult = RulesScriptResultFetcher.fetchGroupResult(script, parameters)
      checkMissingGroupParameters(scriptResult.notValidatedParameters)
      scriptResult
    }
  }

  /**
   * Runs a main rules script that includes all others.
   */
  private RulesScript runMainScript(Class<? extends Script> scriptClass,
      Map<String, Map<String,Object>> parameters, Map<String, Object> environment) {
    if (!scriptClass.getName().endsWith(RULES_FILE_SUFFIX)) {
       throw new InvalidScriptException('Rules script should end with the suffix Grules')
    }
    RulesScript script = rulesScriptFactory.newInstanceMain(scriptClass, parameters, environment)
    if (config.isMultithreadingEnabled()) {
      try {
        GParsExecutorsPool.withPool {
          GParsExecutorsPoolUtil.callAsync { runScript(script) }.get()
        }
      } catch (ExecutionException e) {
        throw e.cause
      }
    } else {
      runScript(script)
    }
    script
  }

  /**
   * Runs an included rules script.
   */
  protected void runIncludedScript(Class<? extends Script> scriptClass,
      List<Class<? extends Script>> scriptsChain,
      Binding binding,
      Map<String, Set<String>> missingRequiredParameters,
      Map<String, Map<String, ValidationErrorProperties>> invalidParameters,
      Map<String, Set<String>> parametersWithMissingDependency,
      Set<String> nologParameters) {
    RulesScript rulesScript = rulesScriptFactory.newInstanceIncluded(scriptClass, scriptsChain,
      binding, missingRequiredParameters, invalidParameters, parametersWithMissingDependency, nologParameters)
    runScript(rulesScript)
  }

  /**
   * Runs a rules script.
   */
  private void runScript(RulesScript script) {
    use(Operators.declaredClasses as List, script.&applyRules)
  }

  /**
   * Checks missing parameters for a rules script with several sections.
   */
  private void checkMissingGroupParameters(Map<String, Map<String, String>> notValidatedParameters) {
    if (!notValidatedParameters.isEmpty()) {
      checkMissingParameters(new NotValidatedParametersGroupException(notValidatedParameters))
    }
  }

  /**
   * Checks missing parameters for a rules script with one default group.
   */
  private void checkMissingFlatParameters(Map<String, String> notValidatedParameters) {
    if (!notValidatedParameters.isEmpty()) {
      checkMissingParameters(new NotValidatedParametersFlatException(notValidatedParameters))
    }
  }

  private void checkMissingParameters(NotValidatedParametersException notValidatedParametersException) {
    OnValidationEventAction onValidationEventAction = config.getNotValidatedParametersAction()
    if (onValidationEventAction != OnValidationEventAction.IGNORE) {
      switch (onValidationEventAction) {
        case OnValidationEventAction.ERROR:
          throw notValidatedParametersException
        case OnValidationEventAction.LOG:
          GrulesLogger.warn("Missing parameters: $notValidatedParametersException.parameters")
          break
        default:
          throw new ConfigException("Unknown action value ($onValidationEventAction)")
      }
    }
  }
}

