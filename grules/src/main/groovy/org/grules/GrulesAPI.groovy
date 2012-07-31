package org.grules

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.Cookie

import org.grules.script.RulesScriptGroupResult
import org.grules.script.RulesScriptResult


/** @see Grules */
class GrulesAPI {

  /** @see Grules#applyGroupRules(Class, java.util.Map) */
  static RulesScriptGroupResult applyGroupRules(Class<? extends Script> rulesScript,
      Map<String, Map<String, Object>> parameters, Map<String, Object> environment = [:]) {
    Closure<RulesScriptGroupResult> preprocessor = newGroupRulesApplicator(rulesScript, environment)
    preprocessor(parameters)
  }

  /** @see Grules#applyRules(Class, java.util.Map, java.util.Map) */
  static RulesScriptResult applyRules(Class<? extends Script> rulesScript, Map<String, Object> parameters,
      Map<String, Object> environment = [:]) {
    Closure<RulesScriptResult> preprocessor = newRulesApplicator(rulesScript, environment)
    preprocessor(parameters)
  }

  /** @see Grules#newGroupRulesApplicator(Class, java.util.Map) */
  static Closure<RulesScriptGroupResult> newGroupRulesApplicator(Class<? extends Script> rulesScript,
      Map<String, Object> environment = [:]) {
    GrulesInjector.ruleEngine.newGroupExecutor(rulesScript, environment)
  }

  /** @see Grules#newRulesApplicator(Class, java.util.Map) */
  static Closure<RulesScriptResult> newRulesApplicator(Class<? extends Script> rulesScript,
      Map<String, Object> environment = [:]) {
    GrulesInjector.ruleEngine.newExecutor(rulesScript, environment)
  }

  /** @see Grules#fetchRequestHeaders(HttpServletRequest) */
  static Map<String, Map<String, Object>> fetchRequestHeaders(HttpServletRequest request) {
    Enumeration<String> headerNames = request.headerNames
    headerNames.toList().collectEntries {  String name ->
      [(name): request.getHeader(name)]
    }
  }

  /** @see Grules#fetchRequestParameters(HttpServletRequest, java.util.List) */
  static Map<String, Map<String, Object>> fetchRequestParameters(HttpServletRequest request,
      List<String> listParameters = []) {
    request.parameterMap.collectEntries { String name, String[] values ->
       [(name): name in listParameters ? values as List<String> : values[0]]
    }
  }

  /** @see Grules#fetchRequestCookies(HttpServletRequest) */
  static Map<String, Map<String, String>> fetchRequestCookies(HttpServletRequest request) {
    (request.cookies as List<Cookie>).collectEntries { Cookie cookie ->
        [(cookie.name): cookie.value]
    }
  }
}