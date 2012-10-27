package org.grules.script

import static org.grules.TestRuleEntriesFactory.*
import static org.grules.TestScriptEntities.*

import org.codehaus.groovy.runtime.MethodClosure
import org.grules.EmptyRulesScript
import org.grules.GrulesInjector
import org.grules.GrulesLogger
import org.grules.ValidationErrorProperties
import org.grules.ValidationException
import org.grules.config.DefaultFunctionFactory
import org.grules.config.GrulesConfig
import org.grules.functions.lib.StringFunctions
import org.grules.script.expressions.Subrule
import org.grules.script.expressions.SubrulesSeq
import org.grules.script.expressions.SubrulesSeqWrapper

import spock.lang.Specification

class IncludeTestScript extends Script {
  def run() {
    binding.setProperty(GROUP, binding.getProperty(GROUP) +
        [(PARAMETER_NAME): PARAMETER_VALUE])
    binding.setProperty(VARIABLE_NAME, VARIABLE_VALUE)
    (this as RulesScript).missingRequiredParameters.put(GROUP, PARAMETER_NAME_AUX)
  }
}

class IncludeTestScriptLastGroup extends Script {
  def run() {
    (this as RulesScript).changeGroup(GROUP_AUX)
    binding.setProperty(VARIABLE_NAME_AUX, binding.getProperty(PARAMETER_NAME_AUX))
  }
}

class RulesScriptTest extends Specification {

  RulesScript script
  GrulesConfig config
  def MISSING_PROPERTY_NAME = 'missingProperty'

  def setupSpec() {
    GrulesLogger.turnOff()
  }

  def setup() {
    config = Mock()
    config.getGroups() >> TEST_CONFIG.groups
    config.getDefaultGroup() >> TEST_CONFIG.defaultGroup
    script = new RulesScript()
    script.initMain(new EmptyRulesScript(), config, GrulesInjector.ruleEngine,
        [(GROUP): [(PARAMETER_NAME): PARAMETER_VALUE]], [:])
  }

  def "Initializing sets variables for dirty values"() {
    expect:
      script.variables.containsKey(RulesBinding.toDirtyParameterName(PARAMETER_NAME))
  }

  def "Initializing does not add variables with clean values"() {
    expect:
      !script.variables.containsKey(PARAMETER_NAME)
  }

  def "changeGroup removes old variables"() {
    setup:
      script.variablesBinding.addCleanParameterValue(PARAMETER_NAME, PARAMETER_VALUE, GROUP)
      script.changeGroup(GROUP_AUX)
    expect:
      !script.variables.containsKey(RulesBinding.toDirtyParameterName(PARAMETER_NAME))
      !script.variables.containsKey(PARAMETER_NAME)
  }

  def "changeGroup adds new variables"() {
    setup:
      (script.variables.get(GROUP_AUX) as Map).put(PARAMETER_NAME_AUX, PARAMETER_VALUE)
      script.changeGroup(GROUP_AUX)
    expect:
      script.variables.containsKey(PARAMETER_NAME_AUX)
  }

  def "changeGroup throws exception if group is missing"() {
    when:
      script.changeGroup('missingGroup')
    then:
      thrown(InvalidGroupException)
  }

  def "Include adds variables to main script"() {
    setup:
      script.include(IncludeTestScript)
    expect:
      (script.variables[GROUP] as Map).containsKey(PARAMETER_NAME)
      script.variables.containsKey(PARAMETER_NAME)
      script.variables.containsKey(VARIABLE_NAME)
  }

  def "Include does not contain variables from last group"() {
    setup:
      script.variables.get(GROUP_AUX).putAt(PARAMETER_NAME_AUX, PARAMETER_VALUE)
      script.include(IncludeTestScriptLastGroup)
    expect:
      !script.variables.containsKey(PARAMETER_NAME_AUX)
  }

  def "Included script has access to parent script fields"() {
    setup:
      script.include(IncludeTestScript)
    expect:
      script.missingRequiredParameters.containsKey(GROUP)
  }

  def "applyRule saves the result for valid parameter"() {
    setup:
      script.applyRule(PARAMETER_NAME, PARAMETER_VALUE, createEmptyRuleClosure())
    expect:
      script.variables.containsKey(PARAMETER_NAME)
  }

  def "Default functions are applied"() {
    setup:
      def trim = StringFunctions.&trim
      def trimFunction = DefaultFunctionFactory.create(trim)
      config.defaultFunctions >> [trimFunction]
      def parameterValue = ' ' + PARAMETER_VALUE
    when:
      script.applyRule(PARAMETER_NAME, parameterValue, createEmptyRuleClosure())
    then:
      script.variables[PARAMETER_NAME] == trim(parameterValue)
  }

  def "Default function can be skipped"() {
    setup:
      MethodClosure trim = StringFunctions.&trim
      def trimFunction = DefaultFunctionFactory.create(trim)
      config.defaultFunctions >> [trimFunction]
      def parameterValue = ' ' + PARAMETER_VALUE
      def skipFunction = script.skip(trim.method)
      def subrulesSeq = SubrulesSeqWrapper.wrap(skipFunction)
    when:
      script.applyRule(PARAMETER_NAME, parameterValue) {subrulesSeq}
    then:
      script.variables[PARAMETER_NAME] == parameterValue
  }

  def "If default function fails subrule index is negative"() {
    setup:
      def failingSubrule = createFailingSubrule()
      config.getDefaultFunctions() >> [failingSubrule]
      def failingSubruleIndex = -(config.getDefaultFunctions().indexOf(failingSubrule) + 1)
      script.applyRule(PARAMETER_NAME, PARAMETER_VALUE, createEmptyRuleClosure())
    expect:
      script.invalidParameters.containsKey(GROUP)
      script.invalidParameters[GROUP][PARAMETER_NAME].subruleIndex == failingSubruleIndex
  }

  def "applyRuleToOptionalParameter saves the result for valid parameter with default value"() {
    setup:
      script.applyRuleToOptionalParameter(PARAMETER_NAME_AUX, createEmptyRuleClosure(), '')
    expect:
      script.variables.containsKey(PARAMETER_NAME_AUX)
  }

  def "applyRuleToRequiredParameter saves a parameter as missing if it is required but empty"() {
    setup:
      (script.variables[GROUP] as Map).put(PARAMETER_NAME, '')
      script.applyRuleToRequiredParameter(PARAMETER_NAME, createEmptyRuleClosure())
    expect:
      script.missingRequiredParameters == [(GROUP): [PARAMETER_NAME] as Set]
  }

  def "applyRuleToRequiredParameter saves a parameter as missing if it is required but null"() {
    setup:
      (script.variables[GROUP] as Map).put(PARAMETER_NAME, null)
      script.applyRuleToRequiredParameter(PARAMETER_NAME, createEmptyRuleClosure())
    expect:
      script.missingRequiredParameters == [(GROUP): [PARAMETER_NAME] as Set]
  }

  def "applyRuleToRequiredParameter saves a parameter as missing if its value is not available"() {
    setup:
      script.applyRuleToRequiredParameter(PARAMETER_NAME_AUX, createEmptyRuleClosure())
    expect:
      script.missingRequiredParameters == [(GROUP): [PARAMETER_NAME_AUX] as Set]
  }

  def "applyRule saves a parameter with absent dependant parameter as with missing dependency"() {
    setup:
      SubrulesSeq rule = Mock()
      rule.apply(_) >> {throw new MissingParameterException(GROUP, PARAMETER_NAME_AUX)}
      script.applyRule(PARAMETER_NAME, PARAMETER_VALUE) {rule}
    expect:
      script.missingRequiredParameters == [(GROUP): [PARAMETER_NAME_AUX] as Set]
      script.parametersWithMissingDependency == [(GROUP): [PARAMETER_NAME] as Set]
  }

  def "applyRule saves a parameter as invalid if its value is invalid"() {
    setup:
      SubrulesSeq rule = Mock()
      rule.apply(_) >> {throw new ValidationException()}
      script.applyRule(PARAMETER_NAME, PARAMETER_VALUE) {rule}
    expect:
      script.invalidParameters.containsKey(GROUP)
      script.invalidParameters.get(GROUP).containsKey(PARAMETER_NAME)
  }

  def "propertyMissing throws InvalidDependencyParameterException for invalid value"() {
    when:
      script.invalidParameters.put(GROUP, [(PARAMETER_NAME): new ValidationErrorProperties()])
      script.propertyMissing(PARAMETER_NAME)
    then:
      thrown(InvalidDependencyValueException)
  }

  def "propertyMissing throws InvalidDependencyParameterException for missing value"() {
    when:
      script.missingRequiredParameters.put(GROUP, [PARAMETER_NAME] as Set)
      script.propertyMissing(PARAMETER_NAME)
    then:
      thrown(InvalidDependencyValueException)
  }

  def "propertyMissing throws InvalidDependencyParameterException for parameter with missing dependency"() {
    when:
      script.parametersWithMissingDependency.put(GROUP, [PARAMETER_NAME] as Set)
      script.propertyMissing(PARAMETER_NAME)
    then:
      thrown(InvalidDependencyValueException)
  }

  def "propertyMissing throws MissingPropertyException for absent parameter"() {
    when:
      script.propertyMissing(MISSING_PROPERTY_NAME)
    then:
      thrown(MissingPropertyException)
  }

  def "propertyMissing throws MissingPropertyException for non existing parameter in group"() {
    when:
      script.propertyMissing(MISSING_PROPERTY_NAME, GROUP)
    then:
      thrown(MissingPropertyException)
  }

  def "propertyMissing throws MissingParameterException for dirty value of non existing parameter in group"() {
    when:
      script.propertyMissing(RulesBinding.toDirtyParameterName(MISSING_PROPERTY_NAME), GROUP)
    then:
      thrown(MissingParameterException)
  }

  def "Reading parameter dirty value from empty group causes MissingParameterException"() {
    when:
      (script.variables[GROUP_AUX] as Map).get(RulesBinding.toDirtyParameterName(MISSING_PROPERTY_NAME))
    then:
      thrown(MissingParameterException)
  }

  def "Reading parameter clean value from empty group causes MissingPropertyException"() {
    when:
      (script.variables[GROUP_AUX] as Map).get(MISSING_PROPERTY_NAME)
    then:
      thrown(MissingPropertyException)
  }

  def "fetchNotValidatedParameters returns not validated parameters"() {
    setup:
      def notValidatedParameters = script.fetchNotValidatedParameters()
    expect:
      notValidatedParameters.containsKey(GROUP)
      notValidatedParameters.get(GROUP).containsKey(PARAMETER_NAME)
      notValidatedParameters.get(GROUP).get(PARAMETER_NAME) == PARAMETER_VALUE
  }

  def "fetchNotValidatedParameters does not return clean parameters"() {
    setup:
      script.variablesBinding.addCleanParameterValue(PARAMETER_NAME, CLEAN_PARAMETER_VALUE, GROUP)
    expect:
      script.fetchNotValidatedParameters().isEmpty()
  }

  def "fetchNotValidatedParameters does not return invalid parameters"() {
    setup:
      script.invalidParameters.put(GROUP, [(PARAMETER_NAME): new ValidationErrorProperties()])
    expect:
      script.fetchNotValidatedParameters().isEmpty()
  }

  def "fetchNotValidatedParameters gets parameters from missing group"() {
    setup:
      Map auxGroup = script.variables.get(GROUP_AUX)
      auxGroup.put(RulesBinding.toDirtyParameterName(PARAMETER_NAME_AUX), PARAMETER_VALUE)
      def missingParameters = script.fetchNotValidatedParameters()
    expect:
      missingParameters.containsKey(GROUP_AUX)
      missingParameters.get(GROUP_AUX).containsKey(PARAMETER_NAME_AUX)
      missingParameters.get(GROUP_AUX).get(PARAMETER_NAME_AUX) == PARAMETER_VALUE
  }

  def "fetchEnvironment contains variables but not groups"() {
    setup:
      script.variables.put(VARIABLE_NAME, VARIABLE_VALUE)
      script.applyRules()
      def environment = script.fetchEnvironment()
    expect:
      environment.containsKey(VARIABLE_NAME)
      !environment.containsKey(GROUP)
      !environment.containsKey(RulesBinding.toDirtyParameterName(PARAMETER_NAME))
  }

  def "adds group variables and variables for default group"() {
    expect:
      (script.variables[GROUP] as Map).containsKey(RulesBinding.toDirtyParameterName(PARAMETER_NAME))
      script.variables.containsKey(RulesBinding.toDirtyParameterName(PARAMETER_NAME))
  }

  def "MissingPropertyException is thrown if group variables is missing"() {
    when:
      script.variables[GROUP][PARAMETER_NAME_AUX]
    then:
      thrown(MissingPropertyException)
  }

  def "After applying rules direct parameters variables from last group are removed"() {
    when:
      script.applyRules()
    then:
      !(PARAMETER_NAME in script.variables)
      !(RulesBinding.toDirtyParameterName(PARAMETER_NAME) in script.variables)
  }

  def "Fetching clean parameters"() {
    when:
      script.applyRule(PARAMETER_NAME, PARAMETER_VALUE, createEmptyRuleClosure())
      (script.variables.get(GROUP) as Map).put(RulesBinding.toDirtyParameterName(PARAMETER_NAME), PARAMETER_VALUE)
      script.variables.put(VARIABLE_NAME, VARIABLE_VALUE)
    then:
      script.fetchCleanParameters().containsKey(GROUP)
      script.fetchCleanParameters()[GROUP] == [(PARAMETER_NAME): PARAMETER_VALUE]
  }
}