ruleset {
  ruleset('rulesets/basic.xml')
  ruleset('rulesets/braces.xml')
  ruleset('rulesets/concurrency.xml')
  ruleset('rulesets/convention.xml')
  ruleset('rulesets/design.xml')
  ruleset('rulesets/dry.xml')
  ruleset('rulesets/exceptions.xml')
  ruleset('rulesets/formatting.xml')
  ruleset('rulesets/generic.xml')
  ruleset('rulesets/groovyism.xml')
  ruleset('rulesets/imports.xml')
  ruleset('rulesets/junit.xml') {
    exclude 'JUnitPublicNonTestMethod'
  }
  ruleset('rulesets/logging.xml')
  ruleset('rulesets/naming.xml') {
    exclude 'FactoryMethodName'
    exclude 'MethodName'
    exclude 'PropertyName'
  }
  ruleset('rulesets/security.xml')
  ruleset('rulesets/serialization.xml')
  ruleset('rulesets/size.xml') {
    exclude 'MethodCount'
  }
  ruleset('rulesets/unnecessary.xml') {
    exclude 'UnnecessaryPackageReference'
    exclude 'UnnecessaryPublicModifier'
    // TODO remove when spock 0.7 is available
    exclude 'UnnecessaryGetter'
  }
  ruleset('rulesets/unused.xml')
}