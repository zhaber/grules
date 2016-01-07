ruleset {
  ruleset('rulesets/basic.xml') {
    exclude 'MultipleUnaryOperators'
  }
  ruleset('rulesets/braces.xml')
  ruleset('rulesets/concurrency.xml')
  ruleset('rulesets/convention.xml') {
    exclude 'NoDef'
  }
  ruleset('rulesets/design.xml') {
    exclude 'Instanceof'
  }
  ruleset('rulesets/dry.xml')
  ruleset('rulesets/exceptions.xml')
  ruleset('rulesets/formatting.xml') {
    exclude 'ClassJavadoc'
  }
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
    exclude 'UnnecessaryGetter'
  }
  ruleset('rulesets/unused.xml')
}
