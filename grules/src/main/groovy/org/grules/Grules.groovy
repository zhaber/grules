package org.grules

import javax.servlet.http.HttpServletRequest

import org.grules.script.RulesScriptGroupResult
import org.grules.script.RulesScriptResult

/**
 * The main class for interacting with grules functionality.
 */
class Grules {

  /**
   * Name for rule application with combined parameters is built by concatenation of their names using
   * <code>COMBINED_PARAMETERS_SEPARATOR}</code> as a separator.
   */
  static final String COMBINED_PARAMETERS_SEPARATOR = ','

  /**
   * Runs a rules script against grouped parameters. The parameters are defined as a map which keys are group names and
   * values are maps of (paramater name -> parameter value) pairs. Usage example:
   * <code><br><br>
   * RulesScriptGroupResult result = Grules.applyGroupRules(TestScriptRules, GET: [id: 1], POST: [login: "admin"])<br>
   * </code>
   * @param rulesScript rules script class
   * @param parameters input parameters
   * @param environment custom functions and variables
   * @return result of rules application
   */
  RulesScriptGroupResult applyGroupRules(Class<? extends Script> rulesScript,
      Map<String, Map<String, Object>> parameters, Map<String, Object> environment = [:]) {
    GrulesAPI.applyGroupRules(rulesScript, parameters, environment)
  }

  /**
   * Runs a rules script against non-grouped parameters. The parameters are defined as a map of (paramater name ->
   * parameter value) pairs. Usage example:
   * <code><br><br>
   * RulesScriptResult result = Grules.applyRules(TestScriptRules, id: 1, login: "admin")<br>
   * </code>
   * @param rulesScript rules script class
   * @param parameters input parameters
   * @param environment custom functions and variables
   * @return result of rules application
   */
  RulesScriptResult applyRules(Class<? extends Script> rulesScript, Map<String, Object> parameters,
      Map<String, Object> environment = [:]) {
    GrulesAPI.newRulesApplicator(rulesScript, parameters, environment)
  }

  /**
   * Creates a closure that runs a given rules script against passed parameters (grouped). Usage example:
   * <code><br><br>
   * def test = Grules.newGroupRulesApplicator(TestScriptRules)<br>
   * RulesScriptGroupResult result1 = test(GET: [id: 1, style: "default"], POST: [login: "admin"])
   * RulesScriptGroupResult result2 = test(GET: [id: 2])
   * </code>
   * @param rulesScript rules script class
   * @param environment custom functions and variables
   * @return closure that runs the script
   */
  Closure<RulesScriptGroupResult> newGroupRulesApplicator(Class<? extends Script> rulesScript,
      Map<String, Object> environment = [:]) {
    GrulesAPI.newGroupRulesApplicator(rulesScript, environment)
  }

  /**
   * Creates a closure that runs a given rules script against passed parameters (ungrouped). Usage example:
   * <code><br><br>
   * def test = Gruless.newRulesApplicator(AllInOneRules)<br>
   * RulesScriptResult result1 = test(id: 1, style: "default")
   * RulesScriptResult result2 = test(id: 2)
   *
   * @param rulesScript rules script class
   * @param environment custom functions and variables
   * @return closure that runs the script
   */
  Closure<RulesScriptResult> newRulesApplicator(Class<? extends Script> rulesScript,
      Map<String, Object> environment = [:]) {
    GrulesAPI.newRulesApplicator(rulesScript, environment)
  }

  /**
   * Creates a map with request HTTP headers from the given request object.
   *
   * @param request HTTP request object
   * @return map with headers
   */
  Map<String, Map<String, Object>> fetchRequestHeaders(HttpServletRequest request) {
    GrulesAPI.fetchRequestHeaders(request)
  }

 /**
  * Creates a map with HTTP parameters from the given request object.
  *
  * @param request HTTP request object
  * @param listParameters collection of parameters that represent list of values not a single string
  * @return map with parameters, where key is a parameter name and value is a string or a list of strings
  */
  Map<String, Map<String, Object>> fetchRequestParameters(HttpServletRequest request,
      List<String> listParameters = []) {
    GrulesAPI.fetchRequestParameters(request, listParameters)
  }

 /**
  * Creates a map with cookies from the given request object.
  *
  * @param request HTTP request object
  * @return map with cookies
  */
  Map<String, Map<String, String>> fetchRequestCookies(HttpServletRequest request) {
    GrulesAPI.fetchRequestCookies(request)
  }

}
