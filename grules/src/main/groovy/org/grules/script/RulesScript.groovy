package org.grules.script

import org.grules.GrulesLogger
import org.grules.ValidationErrorProperties
import org.grules.ValidationException
import org.grules.config.GrulesConfig
import org.grules.script.expressions.InvalidValidatorException
import org.grules.script.expressions.Skip
import org.grules.script.expressions.Subrule
import org.grules.script.expressions.SubrulesSeq
import org.grules.script.expressions.SubrulesSeqWrapper

import com.google.common.base.Optional

/**
 * Encapsulates state of a rules script.
 */
class RulesScript implements RulesScriptAPI {

  private GrulesConfig config
  private RuleEngine ruleEngine
  private Script script
  private List<Class<? extends Script>> parentScripts
  private Set<String> nologParameters
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
  private void init(Script script, GrulesConfig config, RuleEngine ruleEngine) {
    this.script = script
    this.variablesBinding = script.binding as RulesBinding
    this.config = config
    this.currentGroup = config.getDefaultGroup()
    this.ruleEngine = ruleEngine
  }

  /**
   * Initialize a main rules script that includes all others.
   *
   * @param script main script
   * @param parameters input parameters
   * @param environment custom variables that have to be included in the script
   */
  protected void initMain(Script script, GrulesConfig config, RuleEngine ruleEngine,
        Map<String, Map<String, Object>> parameters, Map<String, Object> environment) {
    init(script, config, ruleEngine)
    parentScripts = []
    nologParameters = []
    missingRequiredParameters = [:].withDefault { [] as Set<String> }
    invalidParameters = [:].withDefault { [:] }
    parametersWithMissingDependency = [:].withDefault { [] as Set<String> }
    variablesBinding.addRawValuesVariables(parameters, this.&propertyMissing)
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
  protected void initInclude(Script script, GrulesConfig config, RuleEngine ruleEngine,
      List<Class<? extends Script>> parentScripts,
      Map<String, Set<String>> missingRequiredParameters, Map<String,
      Map<String, ValidationErrorProperties>> invalidParameters,
      Map<String, Set<String>> parametersWithMissingDependency,
      Set<String> nologParameters) {
    init(script, config, ruleEngine)
    this.parentScripts = parentScripts
    this.missingRequiredParameters = missingRequiredParameters
    this.invalidParameters = invalidParameters
    this.parametersWithMissingDependency = parametersWithMissingDependency
    this.nologParameters = nologParameters
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
    if (!(group in config.getGroups())) {
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
   * Returns parameters that passed preprocessing by the rules script.
   */
  Map<String, Map<String, Object>> fetchCleanParameters() {
    variablesBinding.fetchCleanParametersValues()
  }

  /**
   * Returns all script variables that are not parameters.
   */
  Map<String, Object> fetchEnvironment() {
    variablesBinding.fetchEnvironment()
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
    ruleEngine.runIncludedScript(includedScriptClass, scriptsChain, variablesBinding, missingRequiredParameters,
        invalidParameters, parametersWithMissingDependency, nologParameters)
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
      def value = parameterValue
      try {
        List<Subrule> defaultFunctions = config.getDefaultFunctions().findAll { Subrule defaultFunction ->
          !subrulesSeq.hasSkipFunction(defaultFunction)
        }
        value = SubrulesSeqWrapper.wrap(defaultFunctions).apply(parameterValue)
      } catch (ValidationException e) {
        e.errorProperties.subruleIndex = -e.errorProperties.subruleIndex
        throw e
      }
      def cleanValue = subrulesSeq.apply(value)
      variablesBinding.addCleanParameterValue(parameterName, cleanValue, currentGroup)
      if (!(parameterName in nologParameters)) {
        GrulesLogger.info(parameterName + ' = ' + parameterValue)
      }
    } catch (MissingParameterException e) {
      missingRequiredParameters[e.group].add(e.parameterName)
      parametersWithMissingDependency[currentGroup].add(parameterName)
    } catch (ValidationException e) {
      invalidParameters[currentGroup].put(parameterName, e.errorProperties)
      if (parameterName in nologParameters) {
        e.errorProperties.value = 'NOLOG_PARAMETER'
      }
      GrulesLogger.info("Parameter $parameterName failed validation. Validation error properties: " + e.errorProperties)
    } catch (InvalidDependencyValueException e) {
      parametersWithMissingDependency[currentGroup].add(parameterName)
    } catch (InvalidValidatorException e) {
      throw new InvalidValidatorException(e.returnValue, e.methodName, parameterName)
    }
  }

  /**
   * Applies a rule to a list of parameters.
   *
   * @param ruleName a name of the rule used for reports
   * @param requiredParameters a set of required parameters
   * @param optionalParameters a map of parameters names and their default values
   * @param subrulesSeqClosure closure that returns a subrules sequence
   */
  @Override
  void applyRuleToParametersList(String ruleName, Set<String> ruleRequiredParameters,
      Map<String, Object> optionalParameters, Closure<SubrulesSeq> subrulesSeqClosure) {
    Map<String, Optional> ruleRequiredParametersValues = ruleRequiredParameters.collectEntries {
      String parameterName ->
      [(parameterName):variablesBinding.fetchValue(currentGroup, parameterName)]
    }
    Set<String> missingRuleRequiredParameters = ((ruleRequiredParametersValues.findAll {
      String parameterName, Optional value ->
      !value.isPresent()
    }) as Map<String, Object>).keySet()
    if (missingRuleRequiredParameters.isEmpty()) {
      Map<String, Object> optionalParametersValues = optionalParameters.collectEntries {
          String parameterName, defaultValue ->
        Optional parameterValue = variablesBinding.fetchValue(currentGroup, parameterName)
        [(parameterName):parameterValue | defaultValue]
      }
      List<Object> parametersValues = ruleRequiredParametersValues.values()*.get() + optionalParametersValues.values()
      applyRule(ruleName, parametersValues, subrulesSeqClosure)
    } else {
      missingRuleRequiredParameters.each { String parameterName ->
        missingRequiredParameters[currentGroup].add(parameterName)
      }
    }
  }

  /**
   * Applies a preprocessing rule to a required parameter.
   *
   * @param parameterName parameter name
   * @param subrulesSeqClosure closure that returns a subrules sequence
   */
  @Override
  void applyRuleToRequiredParameter(String parameterName, Closure<SubrulesSeq> subrulesSeqClosure) {
    Optional parameterValue = variablesBinding.fetchValue(currentGroup, parameterName)
    if (parameterValue.isPresent()) {
      applyRule(parameterName, parameterValue.get(), subrulesSeqClosure)
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
    Optional parameterValue = variablesBinding.fetchValue(currentGroup, parameterName)
    applyRule(parameterName, parameterValue | defaultValue, subrulesSeqClosure)
  }

  /**
   * Construct an error properties map.
   */
  @Override
  ValidationErrorProperties e(Map<String, Object> properties) {
    new ValidationErrorProperties(properties)
  }

  /**
   * Construct an error properties map.
   */
  @Override
  ValidationErrorProperties e(String errorMessage, Map<String, Object> properties) {
    new ValidationErrorProperties(errorMessage, properties)
  }

  /**
   * Construct an error properties map.
   */
  @Override
  ValidationErrorProperties e(Map<String, Object> properties, String errorMessage) {
    new ValidationErrorProperties(errorMessage, properties)
  }

  /**
   * Construct an error properties map.
   */
  @Override
  ValidationErrorProperties e(String errorMessage) {
    new ValidationErrorProperties(errorMessage)
  }

  /**
   * Handles reading of missing parameters and clean values.
   *
   * @param name the missing parameter name
   * @param group the group name
   */
  void propertyMissing(String name, String providedGroup = '') {
    String group = providedGroup.isEmpty() ? currentGroup : providedGroup
    if (RulesBinding.isRawParameterName(name)) {
      throw new MissingParameterException(group, RulesBinding.parseDirtyParameterName(name))
    } else if (isProcessedParameter(group, name)) {
      throw new InvalidDependencyValueException()
    } else {
      throw new MissingPropertyException(name)
    }
  }

  private boolean isProcessedParameter(String group, String name) {
    boolean isInvalidParameter = invalidParameters.containsKey(group) && invalidParameters[group].containsKey(name)
    boolean isMissingRequiredParameter = name in missingRequiredParameters[group]
    boolean isParameterWithMissingDependency = name in parametersWithMissingDependency[group]
    boolean isCleanParameter = variablesBinding.isCleanParameter(group, name)
    isInvalidParameter || isMissingRequiredParameter || isParameterWithMissingDependency || isCleanParameter
  }

  /**
   * Fetches all parameters that were not validated.
   */
  Map<String, Map<String, Object>> fetchNotValidatedParameters() {
    variablesBinding.fetchParametersDirtyValues().collectEntries {
      String groupName, Map<String, Object> groupParameters ->
      Map<String, Object> notValidatedParameters = groupParameters.findAll {
        String parameterName, parameterValue ->
        !isProcessedParameter(groupName, parameterName)
      }
      notValidatedParameters.isEmpty() ? [:] : [(groupName):notValidatedParameters]
    }
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

  /**
   * A validation block.
   */
  @Override
  void validate(Closure<Map<String, Object>> closure) {
    try {
      closure().each { String name, value ->
        variablesBinding.addCleanParameterValue(name, value, currentGroup)
      }
    } catch (ValidationException e) {
      invalidParameters[currentGroup].put(e.errorProperties.parameter, e.errorProperties)
    }
  }

  @Override
  Skip skip(String... functions) {
    new Skip(functions)
  }

  @Override
  void addParameter(String name, value) {
    variablesBinding.addParameter(name, value, currentGroup)
  }

  @Override
  void nolog(String... parameters) {
    nologParameters.addAll(parameters)
  }
}

