package org.grules.script;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.MapWithDefault;

import org.grules.GrulesInjector;
import org.grules.config.GrulesConfig;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * Encapsulates script variables. The <code>VariablesBinding</code> class handles parameters dirty/clean values and
 * parameters groups.
 */
class RulesBinding extends Binding {

  private static final String DIRTY_VALUE_PREFIX = "$";
  private static final String RESOURCES_VARIABLE = "m";
  private static final GrulesConfig CONFIG = GrulesInjector.getConfig();
  private static final Set<String> GROUPS = CONFIG.getGroups();
  private static final Map<String, String> MESSAGES = GrulesInjector.getMessageResourceBundle().getMessages();

  private static class MissingGroupPropertyClosure extends Closure<Object> {

    private final String group;
    private final Closure<Object> missingPropertyClosure;

    private MissingGroupPropertyClosure(Object owner, String group, Closure<Object> missingPropertyClosure) {
      super(owner);
      this.group = group;
      this.missingPropertyClosure = missingPropertyClosure;
    }

    @SuppressWarnings("unused")
    private Object doCall(String key) {
      return missingPropertyClosure.call(key, group);
    }
  }

  /**
   * Checks if the specified property is a parameter raw value.
   */
  static boolean isRawParameterName(String name) {
    return name.startsWith(DIRTY_VALUE_PREFIX);
  }

  /**
   * Returns name of a variable for a parameter dirty value.
   */
  static private String toDirtyParameterName(String parameterName) {
    return DIRTY_VALUE_PREFIX + parameterName;
  }

  /**
   * Returns name of a parameter represented by dirty parameter variable <code>dirtyValueVariableName</code>.
   */
  static String parseDirtyParameterName(String dirtyValueVariableName) {
    return dirtyValueVariableName.substring(DIRTY_VALUE_PREFIX.length(), dirtyValueVariableName.length());
  }

  RulesBinding() {
    super(new HashMap<String, Object>() {{put(RESOURCES_VARIABLE, MESSAGES);}});
  }

  /**
   * @param variables script variables
   */
  RulesBinding(Map<String, Object> variables) {
    super(variables);
  }

  /**
   * Checks if a value of parameter <code>parameterName</code> from the group <code>group</code> is part of the input.
   */
  boolean isParameterDefined(String group, String parameterName) {
    return getGroup(group).containsKey(toDirtyParameterName(parameterName));
  }

  private Object fetchDefinedParameterValue(String group, String parameterName) {
    if (getGroup(group).containsKey(parameterName)) {
      return getGroup(group).get(parameterName);
    } else {
      return getGroup(group).get(toDirtyParameterName(parameterName));
    }
  }

  /**
   * Returns value of the parameter <code>parameterName</code> from the <code>group</code>.
   */
  Optional<Object> fetchValue(String group, String parameterName) {
    if (isParameterDefined(group, parameterName)) {
      Object value = fetchDefinedParameterValue(group, parameterName);
      if (value != "" && value != null) {
        return Optional.of(value);
      }
    }
    return Optional.absent();
  }

  /**
   * Returns custom script variables.
   */
  Map<String, Object> fetchEnvironment() {
    Map<String, Object> environment = Maps.newHashMap();
    for (Entry<String, Object> variable : getVariables().entrySet()) {
      if (!GROUPS.contains(variable.getKey()) && variable.getKey() != RESOURCES_VARIABLE) {
        environment.put(variable.getKey(), variable.getValue());
      }
    }
    return environment;
  }

  /**
   * Adds variables for direct access to parameters of the group <code>group</code>, i.e. that a group prefix can be
   * omitted.
   */
  void addGroupParametersVariables(String group) {
    getVariables().putAll(getGroup(group));
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> getVariables() {
    return super.getVariables();
  }

  /**
   * Adds variables that contains raw parameters values.
   *
   * @param parameters input parameters
   * @param missingPropertyClosure closure called when some parameter is missing
   */
  void addRawValuesVariables(Map<String, Map<String, Object>> parameters, Closure<Object> missingPropertyClosure) {
    for (String group : GROUPS) {
      Map<String, Object> dirtyParametersValuesVariables = Maps.newHashMap();
      if (parameters.containsKey(group)) {
        for (Entry<String, Object> parameter: parameters.get(group).entrySet()){
          String dirtyParameterName = toDirtyParameterName(normalizeParameterName(parameter.getKey()));
          dirtyParametersValuesVariables.put(dirtyParameterName, parameter.getValue());
        }
      }
      Closure<Object> missingParameterClosure = new MissingGroupPropertyClosure(this, group, missingPropertyClosure);
      Map<String, Object> dirtyParametersValuesVariablesWithDefault =
          MapWithDefault.newInstance(dirtyParametersValuesVariables, missingParameterClosure);
      getVariables().put(group, dirtyParametersValuesVariablesWithDefault);
    }
  }

  /**
   * Adds a variable for the specified parameter.
   *
   * @param name a parameter name
   * @param value a parameter value
   * @param group a parameter group
   */
  void addParameter(String name, Object value, String group) {
    getGroup(group).put(toDirtyParameterName(normalizeParameterName(name)), value);
  }

  /**
   * Adds variables to a script environment.
   */
  void addCustomVariables(Map<String, Object> customVariables) {
    getVariables().putAll(customVariables);
  }

  private static String normalizeParameterName(String parameterName) {
    if (!parameterName.isEmpty()) {
      String firstLetter = parameterName.substring(0, 1).toLowerCase();
      return firstLetter + (parameterName.length() > 1 ? parameterName.substring(1) : "");
    } else {
      return "";
    }
  }

  /**
   * Adds a clean value for the given parameter from group <code>group</code>.
   */
  @SuppressWarnings("unchecked")
  void addCleanParameterValue(String parameter, Object value, String group) {
    getVariables().put(parameter, value);
    ((Map<String, Object>) getVariables().get(group)).put(parameter, value);
  }

  /**
   * Returns groups with parameters variables.
   */
  @SuppressWarnings("unchecked")
  Map<String, Map<String, Object>> fetchParametersGroupVariables() {
    Map<String, Map<String, Object>> parametersGroupVariables = Maps.newHashMap();
    for (Entry<String, Object> variable : getVariables().entrySet()) {
      if (GROUPS.contains(variable.getKey())) {
        parametersGroupVariables.put(variable.getKey(), (Map<String, Object>) variable.getValue());
      }
    }
    return parametersGroupVariables;
  }

  Map<String, Map<String, Object>> fetchParametersDirtyValues() {
    Map<String, Map<String, Object>> parametersRawValues = Maps.newHashMap();
    for (Entry<String, Map<String, Object>> groupVariable : fetchParametersGroupVariables().entrySet()){
      Map<String, Object> parametersDirtyValues = Maps.newHashMap();
      for (Entry<String, Object> parameter : groupVariable.getValue().entrySet()) {
        if (isRawParameterName(parameter.getKey())) {
          parametersDirtyValues.put(parseDirtyParameterName(parameter.getKey()), parameter.getValue());
        }
      }
      parametersRawValues.put(groupVariable.getKey(), parametersDirtyValues);
    }
    return parametersRawValues;
  }

  /**
   * Checks if the specified parameter was processed and is valid.
   */
  boolean isCleanParameter(String group, String parameterName) {
    return getVariables().containsKey(group) && getGroup(group).containsKey(parameterName);
  }

  /**
   * Returns clean values for all clean parameters.
   */
  Map<String, Map<String, Object>> fetchCleanParametersValues() {
    Map<String, Map<String, Object>> cleanParameters = Maps.newHashMap();
    for (Entry<String, Map<String, Object>> groupVariable : fetchParametersGroupVariables().entrySet()) {
      Map<String, Object> groupCleanParameters = Maps.newHashMap();
      for (Entry<String, Object> parameter : groupVariable.getValue().entrySet()) {
        if (!isRawParameterName(parameter.getKey())) {
          groupCleanParameters.put(parameter.getKey(), parameter.getValue());
        }
      }
      cleanParameters.put(groupVariable.getKey(), groupCleanParameters);
    }
    return cleanParameters;
  }

  /**
   * Removes direct parameters variables for a current group.
   */
  void removeGroupDirectParametersVariables(String group) {
    Set<String> variablesNames = getGroup(group).keySet();
    getVariables().keySet().removeAll(variablesNames);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getGroup(String group) {
    return (Map<String, Object>) getVariables().get(group);
  }

  @Override
  public String toString() {
    return getVariables().toString();
  }
}