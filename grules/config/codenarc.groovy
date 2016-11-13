ruleset {
  ruleset('rulesets/basic.xml') {
    exclude 'EmptyClass' // Used for marker classes
  }
  ruleset('rulesets/braces.xml')
  ruleset('rulesets/concurrency.xml')
  ruleset('rulesets/convention.xml') {
    exclude 'NoDef' // Using def instead of objectas a shorter version
    exclude 'TrailingComma' // Triggers on lists split into two lines
  }
  ruleset('rulesets/design.xml') {
    exclude 'Instanceof' // Downcasting required in AST tranformations
  }
  ruleset('rulesets/dry.xml')
  // ruleset('rulesets/enhanced.xml') // Generates noisy compilation warnings
  ruleset('rulesets/exceptions.xml') {
    exclude 'CatchThrowable' // Any exception can be thrown during transformation
  }
  ruleset('rulesets/formatting.xml')
  ruleset('rulesets/generic.xml')
  ruleset('rulesets/groovyism.xml')
  ruleset('rulesets/imports.xml')
  ruleset('rulesets/logging.xml')
  ruleset('rulesets/naming.xml') {
    exclude 'FactoryMethodName' // Methods that create AST nodes use createX name pattern
  }
  ruleset('rulesets/security.xml') {
    exclude 'JavaIoPackageAccess' // Not EJB project
  }
  ruleset('rulesets/serialization.xml') {
    exclude 'SerializableClassMustDefineSerialVersionUID' // Rule is triggered on enums
  }
  ruleset('rulesets/size.xml') {
    exclude 'CrapMetric' // Generates noisy warnings
    exclude 'ParameterCount' // Required for parameterization of scripts
  }
  ruleset('rulesets/unnecessary.xml') {
    exclude 'UnnecessaryGetter' // A getter method may have implementation
  }
  ruleset('rulesets/unused.xml')
}
