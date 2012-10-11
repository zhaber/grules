package org.grules.script

import org.grules.GrulesInjector
import org.grules.config.GrulesConfig

import com.google.common.base.Optional


/**
 * Encapsulates script variables. The <code>VariablesBinding</code> class handles parameters dirty/clean values and
 * parameters groups.
 */
class RulesBinding extends Binding {

  private static final String DIRTY_VALUE_PREFIX = '$'
  private static final String RESOURCES_VARIABLE = 'm'
  private static final GrulesConfig CONFIG = GrulesInjector.config
  private static final Set<String> GROUPS = CONFIG.groups
  private static final Map<String, String> MESSAGES = GrulesInjector.messageResourceBundle.messages

  /**
   * Checks if the specified property is a parameter raw value.
   */
  static boolean isRawParameterName(String name) {
    name.startsWith(DIRTY_VALUE_PREFIX)
  }

  /**
   * Returns name of a variable for a parameter dirty value.
   */
  static private String toDirtyParameterName(String parameterName) {
    DIRTY_VALUE_PREFIX + parameterName
  }

  /**
   * Returns name of a parameter represented by dirty parameter variable <code>dirtyValueVariableName</code>.
   */
  static String parseDirtyParameterName(String dirtyValueVariableName) {
    dirtyValueVariableName[DIRTY_VALUE_PREFIX.length()..-1]
  }

  RulesBinding() {
    super([(RESOURCES_VARIABLE): MESSAGES])
  }

  /**
   * @param variables script variables
   */
  RulesBinding(Map<String, Object> variables) {
    super(variables)
  }

  /**
   * Checks if a value of parameter <code>parameterName</code> from the group <code>group</code> is part of the input.
   */
  boolean isParameterDefined(String group, String parameterName) {
    (variables[group] as Map<String, Object>).containsKey(toDirtyParameterName(parameterName))
  }

  private fetchDefinedParameterValue(String group, String parameterName) {
    if ((variables[group] as Map<String, Object>).containsKey(parameterName)) {
      variables[group][parameterName]
    } else {
      variables[group][toDirtyParameterName(parameterName)]
    }
  }

  /**
   * Returns value of the parameter <code>parameterName</code> from the <code>group</code>.
   */
  Optional fetchValue(String group, String parameterName) {
    if (isParameterDefined(group, parameterName)) {
      def value = fetchDefinedParameterValue(group, parameterName)
      if (value != '' && value != null) {
        return Optional.of(value)
      }
    }
    Optional.absent()
  }

  Map<String, Object> fetchEnvironment() {
    variables.findAll { String variableName, variableValue ->
      !(variableName in GROUPS) && variableName != RESOURCES_VARIABLE
    }
  }

  /**
   * Adds variables for direct access to parameters of the group <code>group</code>, i.e. that a group prefix can be
   * omitted.
   */
  void addGroupParametersVariables(String group) {
    variables << variables[group]
  }

  /**
   * Adds variables that contains raw parameters value.
   *
   * @param parameters input parameters
   * @param missingPropertyClosure closure called when some parameter is missing
   */
  void addParameters(Map<String, Map<String, Object>> parameters, Closure<Object> missingPropertyClosure) {
    variables << GROUPS.collectEntries {
      String group ->
      Map<String, Object> dirtyParametersValuesVariables = [:]
      if (parameters.containsKey(group)) {
        dirtyParametersValuesVariables += parameters[group].collectEntries {
          String key, value ->
          [(toDirtyParameterName(normalizeParameterName(key))): value]
        }
      }
      [(group): dirtyParametersValuesVariables.withDefault {
          String name ->
          missingPropertyClosure(name, group)
      }]
    }
  }

  /**
   * Adds a variable for the specified parameter.
   *
   * @param name a parameter name
   * @param value a parameter value
   * @param group a parameter group
   */
  void addParameter(String name, value, String group) {
    variables[group][toDirtyParameterName(normalizeParameterName(name))] = value
  }

  /**
   * Adds variables to a script environment.
   */
  void addCustomVariables(Map<String, Object> customVariables) {
    variables << customVariables
  }

  private static String normalizeParameterName(String parameterName) {
    if (!parameterName.isEmpty()) {
      parameterName[0].toLowerCase() + (parameterName.size() > 1 ? parameterName[1..-1] : '')
    } else {
      ''
    }
  }

  /**
   * Adds a clean value for the given parameter from group <code>group</code>.
   */
  void addCleanParameterValue(String parameter, value, String group) {
    variables[parameter] = value
    variables[group][parameter] = value
  }

  /**
   * Returns groups with parameters variables.
   */
  Map<String, Map<String, Object>> fetchParametersGroupVariables() {
    variables.findAll {String name, value -> name in GROUPS}
  }

  Map<String, Map<String, Object>> fetchParametersDirtyValues() {
    fetchParametersGroupVariables().collectEntries {
      String group, Map<String, Object> groupParameters ->
      Map<String, Object> parametersDirtyValues = (groupParameters.collectEntries {
        String parameterName, parameterValue ->
        if (isRawParameterName(parameterName)) {
          [(parseDirtyParameterName(parameterName)) : parameterValue]
        } else {
          [:]
        }
      })
      [(group): parametersDirtyValues]
    }
  }

  /**
   * Checks if the specified parameter was processed and is valid.
   */
  boolean isCleanParameter(String group, String parameterName) {
    variables.containsKey(group) && (variables[group] as Map<String, Object>).containsKey(parameterName)
  }

  /**
   * Returns clean values for all clean parameters.
   */
  Map<String, Map<String, Object>> fetchCleanParametersValues() {
    fetchParametersGroupVariables().collectEntries {
      String group, Map<String, Object> groupParameters ->
      Map<String, Object> parametersDirtyValuesVariables = groupParameters.findAll {
        String name, value ->
        isRawParameterName(name)
      }
      Map<String, Object> cleanParameters = groupParameters - parametersDirtyValuesVariables
      if (cleanParameters.isEmpty()) {
        [:]
      } else {
        [(group): cleanParameters]
      }
    }
  }

  /**
   * Removes direct parameters variables for a current group.
   */
  void removeGroupDirectParametersVariables(String group) {
    Set<String> variablesNames = (variables[group] as Map<String, Object>).keySet()
    variables.keySet().removeAll(variablesNames)
  }

  @Override
  String toString() {
    variables
  }
}
