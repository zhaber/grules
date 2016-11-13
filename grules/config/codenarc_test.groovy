ruleset {
  ruleset('rulesets/basic.xml') {
    exclude 'MultipleUnaryOperators' // Triggers on valid grules expressions, e.g. ~(!a)
  }
  ruleset('rulesets/braces.xml')
  ruleset('rulesets/concurrency.xml')
  ruleset('rulesets/convention.xml') {
    exclude 'NoDef' // Spock tests can use def as return type 
    exclude 'TrailingComma' // Triggers on lists split into two lines
  }
  ruleset('rulesets/design.xml') {
    exclude 'Instanceof' // Downcasting required in AST tranformations
  }
  ruleset('rulesets/dry.xml')
  // ruleset('rulesets/enhanced.xml') // Generates noisy compilation warnings
  ruleset('rulesets/exceptions.xml')
  ruleset('rulesets/formatting.xml') {
    exclude 'ClassJavadoc' // Tests do not need javadoc
  }
  ruleset('rulesets/generic.xml')
  ruleset('rulesets/groovyism.xml')
  ruleset('rulesets/imports.xml')
  ruleset('rulesets/junit.xml') {
    exclude 'JUnitPublicNonTestMethod' // Spock does not have @Test annotation
  }
  ruleset('rulesets/logging.xml')
  ruleset('rulesets/naming.xml') {
    exclude 'FactoryMethodName' // Methods that create AST nodes use createX name pattern
    exclude 'MethodName' // Spock method names have free format
  }
  ruleset('rulesets/security.xml')
  ruleset('rulesets/serialization.xml') {
    exclude 'SerializableClassMustDefineSerialVersionUID' // Rule is triggered on enums
  }
  ruleset('rulesets/size.xml') {
    exclude 'CrapMetric' // Generates noisy warnings
    exclude 'MethodCount' // Number of tests is not bounded
  }
  ruleset('rulesets/unnecessary.xml') {
    exclude 'UnnecessaryGetter' // A getter method may have implementation
  }
  ruleset('rulesets/unused.xml')
}
