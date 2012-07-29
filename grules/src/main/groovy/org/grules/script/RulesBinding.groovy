package org.grules.script

import org.grules.GrulesInjector
import org.grules.config.Config


/**
 * Encapsulates script variables. The <code>VariablesBinding</code> class handles parameters dirty/clean values and
 * parameters groups.
 */
class RulesBinding extends Binding {

  private static final String DIRTY_VALUE_PREFIX = '$'
  private static final String RESOURCES_VARIABLE = 'm'
  private static final Config CONFIG = GrulesInjector.config
  private static final Set<String> GROUPS = CONFIG.groups
  private static final Map<String, String> MESSAGES = GrulesInjector.messagesResourceBundle.messages

  /**
   * Checks if property is a parameter dirty value.
   */
  static boolean isDirtyParameterName(String name) {
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

  /**
   * Returns value of the parameter <code>parameterName</code> from the group <code>group</code>.
   */
  def fetchValue(String group, String parameterName) {
    if (isParameterDefined(group, parameterName)) {
      if ((variables[group] as Map<String, Object>).containsKey(parameterName)) {
        variables[group][parameterName]
      } else {
        variables[group][toDirtyParameterName(parameterName)]
      }
    } else {
      ''
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
   * Adds variables for input parameters.
   *
   * @param parameters input parameters
   * @param missingPropertyClosure closure called when some parameter is missing
   */
  void addParameters(Map<String, Map<String, Object>> parameters, Closure<Object> missingPropertyClosure) {
    variables << GROUPS.collectEntries { String group ->
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
    parameterName.isEmpty() ? '' : parameterName[0].toLowerCase() + parameterName[1..-1]
  }

  /**
   * Adds a clean value for the given parameter from group <code>group</code>.
   */
  void addCleanParameterValue(String parameter, value, String group) {
    variables[parameter] = value
    variables[group][parameter] = value
  }

  /**
   * Returns only parameters variables.
   */
  Map<String, Map<String, Object>> fetchParameters() {
    variables.findAll {String name, value -> name in GROUPS}
  }

  /**
   * Checks if the specified parameter was processed and is valid.
   */
  boolean isCleanParameter(String group, String parameterName) {
    boolean isGroup = variables.containsKey(group) && variables.containsKey(group) instanceof Map<String, Object>
    isGroup && (variables[group] as Map<String, Object>).containsKey(parameterName)
  }

  /**
   * Returns clean values for all clean parameters.
   */
  Map<String, Map<String, Object>> fetchCleanParametersValues() {
    fetchParameters().collectEntries {
      String group, Map<String, Object> groupParameters ->
      Map<String, Object> parametersDirtyValuesVariables = groupParameters.findAll {
        String name, value ->
        isDirtyParameterName(name)
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

  /**
   * Fetches parameters that were not validated by the rules script.
   */
  Map<String, Map<String, String>> fetchNotValidatedParameters() {
    fetchParameters().collectEntries {	String group, Map<String, Object> groupParameters ->
      Map<String, String> notValidatedParameters = (groupParameters.findAll {
        String parameterName, parameterValue ->
        isDirtyParameterName(parameterName) && !groupParameters.containsKey(parseDirtyParameterName(parameterName))
      }).collectEntries({ String parameterName, parameterValue ->
        [(parseDirtyParameterName(parameterName)) : parameterValue]
      })
      notValidatedParameters.isEmpty() ? [:] : [(group): notValidatedParameters]
    }
  }

  @Override
  String toString() {
    variables
  }
}
