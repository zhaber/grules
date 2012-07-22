package org.grules.script

import org.grules.GrulesInjector
import org.grules.ValidationErrorProperties
import org.grules.ValidationException
import org.grules.config.Config
import org.grules.script.expressions.InvalidValidatorException
import org.grules.script.expressions.SubrulesSeq


/**
 * Encapsulates state of a rules script.
 */
class RulesScript implements RulesScriptAPI {

  private static final Config CONFIG = GrulesInjector.config
  private static final Set<String> GROUPS = CONFIG.groups
  private static final RuleEngine RULE_ENGINE = GrulesInjector.ruleEngine

  private Script script
  private List<Class<? extends Script>> parentScripts
  private String currentGroup
  private RulesBinding variablesBinding

  /** Parameters that are missing from input but required by a rules script. */
  Map<String, Set<String>> missingRequiredParameters

  /** Parameters that did no pass validation GroupName -> ParameterName -> ErrorProperties. */
  Map<String, Map<String, ValidationErrorProperties>> invalidParameters

  /** Parameters that depend on other parameters that are missing from input. */
  Map<String, Set<String>> parametersWithMissingDependency

  /**
   * Common initialization logic for all rules scripts.
   *
   * @param script the rules script
   */
  private void init(Script script) {
    this.script = script
    this.variablesBinding = script.binding as RulesBinding
    this.currentGroup = CONFIG.defaultGroup
  }

  /**
   * Initialize a main rules script that includes all others.
   *
   * @param script main script
   * @param parameters input parameters
   */
  void initMain(Script script, Map<String, Map<String, Object>> parameters,
      Map<String, Object> environment) {
    init(script)
    this.parentScripts = []
    missingRequiredParameters = [:].withDefault {[] as Set<String>}
    invalidParameters = [:].withDefault {[:] as Map<String, ValidationErrorProperties>}
    parametersWithMissingDependency = [:].withDefault {[] as Set<String>}
    variablesBinding.addParameters(parameters, this.&propertyMissing)
    variablesBinding.addCustomVariables(environment)
    changeGroup(currentGroup)
  }

  /**
   * Initialize a script included into another rules script.
   *
   * @param script the rules script class
   * @param parentScripts scripts that included this script
   * @param missingRequiredParameters required but missing parameters
   * @param invalidParameters parameters that did not pass validation
   * @param parametersWithMissingDependency parameters that depend on a value of another missing parameter
   */
  void initInclude(Script script, List<Class<? extends Script>> parentScripts,
      Map<String, Set<String>> missingRequiredParameters,	Map<String,
      Map<String, ValidationErrorProperties>> invalidParameters,
      Map<String, Set<String>> parametersWithMissingDependency) {
    init(script)
    this.parentScripts = parentScripts
    this.missingRequiredParameters = missingRequiredParameters
    this.invalidParameters = invalidParameters
    this.parametersWithMissingDependency = parametersWithMissingDependency
    changeGroup(currentGroup)
  }

  /**
   * Adds direct parameters variables to script, so they can be accessed without a group prefix, for example:
   * <code>id</code> instead of <code>POST.id</code>
   *
   * @param group the group to which direct access has to be established
   */
  @Override
  void changeGroup(String group) {
    variablesBinding.removeGroupDirectParametersVariables(currentGroup)
    if (!(group in GROUPS)) {
      throw new InvalidGroupException(group)
    }
    currentGroup = group
    variablesBinding.addGroupParametersVariables(currentGroup)
  }

  /**
   * Script variables.
   */
  Map<String, Object> getVariables() {
    variablesBinding.variables
  }

  /**
   * Parameters that passed preprocessing by the rules script.
   */
  Map<String, Map<String, Object>> fetchCleanParameters() {
    variablesBinding.fetchCleanParametersValues()
  }

  /**
   * Includes a rules script from a given class into this script.
   */
  @Override
  void include(Class<? extends Script> includedScriptClass) {
    List<Class<? extends Script>> scriptsChain = parentScripts + getClass()
    if (includedScriptClass in scriptsChain) {
      throw new CircularIncludeException(scriptsChain + includedScriptClass)
    }
    variablesBinding.removeGroupDirectParametersVariables(currentGroup)
    RULE_ENGINE.runIncludedScript(includedScriptClass, scriptsChain, variablesBinding, missingRequiredParameters,
        invalidParameters, parametersWithMissingDependency)
    variablesBinding.addGroupParametersVariables(currentGroup)
  }

  /**
   * Applies a rule to the given parameter.
   *
   * @param parameterName parameter name
   * @param parameterValue parameter value
   * @param subrulesSeqClosure closure that returns a subrules sequence
   */
  private void applyRule(String parameterName, parameterValue, Closure<SubrulesSeq> subrulesSeqClosure) {
    try {
      SubrulesSeq subrulesSeq = subrulesSeqClosure.call()
      def cleanValue = subrulesSeq.apply(parameterValue)
      variablesBinding.addCleanParameterValue(parameterName, cleanValue, currentGroup)
    } catch (MissingParameterException e){
      missingRequiredParameters[e.group].add(e.parameterName)
      parametersWithMissingDependency[currentGroup].add(parameterName)
    } catch (ValidationException e) {
      invalidParameters[currentGroup].put(parameterName, e.errorProperties)
    } catch (InvalidDependencyParameterException e){
      parametersWithMissingDependency[currentGroup].add(parameterName)
    } catch (InvalidValidatorException e) {
      throw new InvalidValidatorException(e.returnValue, e.methodName, parameterName)
    }
  }

  /**
   * Applies a rule to a list of parameters.
   *
   * @param names list with parameters names
   * @param name how the rule should appear in a preprocessing report
   * @param subrulesSeqClosure closure that returns a subrules sequence
   */
  @Override
  void applyRuleToParametersGroup(List<String> names, String ruleName, Closure<SubrulesSeq> subrulesSeqClosure) {
    List<String> parameterValues = names.collect { String name ->	variablesBinding.fetchValue(currentGroup, name) }
    applyRule(names.toString(), parameterValues, subrulesSeqClosure)
  }

  /**
   * Applies a preprocessing rule to a required parameter.
   *
   * @param parameterName parameter name
   * @param subrulesSeqClosure closure that returns a subrules sequence
   */
  @Override
  void applyRuleToRequiredParameter(String parameterName, Closure<SubrulesSeq> subrulesSeqClosure) {
    def parameterValue = variablesBinding.fetchValue(currentGroup, parameterName)
    if (parameterValue != '') {
      applyRule(parameterName, parameterValue, subrulesSeqClosure)
    } else {
      missingRequiredParameters[currentGroup].add(parameterName)
    }
  }

  /**
   * Applies a preprocessing rule to an optional parameter.
   *
   * @param parameterName parameter name
   * @param subrulesSeqClosure closure that returns a subrules sequence
   * @param defaultValue default parameter value
   */
  @Override
  void applyRuleToOptionalParameter(String parameterName, Closure<SubrulesSeq> subrulesSeqClosure, defaultValue) {
    def parameterValue = variablesBinding.fetchValue(currentGroup, parameterName)
    if (parameterValue != '') {
      applyRule(parameterName, parameterValue, subrulesSeqClosure)
    } else {
      variablesBinding.addCleanParameterValue(parameterName, defaultValue, currentGroup)
    }
  }

  ValidationErrorProperties e(Map<String, Object> properties) {
    new ValidationErrorProperties(properties)
  }

  ValidationErrorProperties e(String message, Map<String, Object> properties) {
    new ValidationErrorProperties(message, properties)
  }

  ValidationErrorProperties e(Map<String, Object> properties, String message) {
    new ValidationErrorProperties(message, properties)
  }

  ValidationErrorProperties e(String message) {
    new ValidationErrorProperties(message)
  }

  /**
   * Handles reading of missing parameters and clean values.
   *
   * @param name the missing parameter name
   * @param group the group name
   */
  void propertyMissing(String name, String providedGroup = '') {
    String group = providedGroup.isEmpty() ? currentGroup : providedGroup
    if (RulesBinding.isDirtyParameterName(name)) {
      throw new MissingParameterException(group, RulesBinding.parseDirtyParameterName(name))
    } else if (isProcessedParameter(group, name)) {
      throw new InvalidDependencyParameterException()
    } else {
      throw new MissingPropertyException(name)
    }
  }

  private boolean isProcessedParameter(String group, String name) {
    boolean isInvalidParameter = invalidParameters[group].containsKey(name)
    boolean isMissingRequiredParameter = name in missingRequiredParameters[group]
    boolean isParameterWithMissingDependency = name in parametersWithMissingDependency[group]
    boolean isCleanParameter = variablesBinding.isCleanParameter(group, name)
    isInvalidParameter || isMissingRequiredParameter || isParameterWithMissingDependency || isCleanParameter
  }

  /**
   * Fetches all parameters that were not validated.
   */
  Map<String, Map<String, String>> fetchNotValidatedParameters() {
    variablesBinding.fetchNotValidatedParameters()
  }

  /**
   * Runs the script to apply preprocessing rules to input parameters.
   */
  void applyRules() {
    script.run()
    variablesBinding.removeGroupDirectParametersVariables(currentGroup)
  }

  @Override
  String toString() {
    variables
  }

  @Override
  void validate(Closure<Map<String, Object>> closure) {
  }

  @Override
  void rules(Closure<Void> closure) {
  }

}
