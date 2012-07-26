ruleset {
  ruleset('rulesets/basic.xml')
  ruleset('rulesets/braces.xml')
  ruleset('rulesets/concurrency.xml')
  ruleset('rulesets/convention.xml')
  ruleset('rulesets/design.xml')
  ruleset('rulesets/dry.xml')
  ruleset('rulesets/exceptions.xml') {
    exclude 'CatchThrowable'
  }
  ruleset('rulesets/formatting.xml')
  ruleset('rulesets/generic.xml')
  ruleset('rulesets/groovyism.xml')
  ruleset('rulesets/imports.xml')
  ruleset('rulesets/logging.xml')
  ruleset('rulesets/naming.xml') {
    exclude 'FactoryMethodName'
  }
  ruleset('rulesets/security.xml') {
    exclude 'JavaIoPackageAccess'
  }
  ruleset('rulesets/serialization.xml') {
    exclude 'SerialVersionUID'
  }
  ruleset('rulesets/size.xml')
  ruleset('rulesets/unnecessary.xml') {
    exclude 'UnnecessaryPackageReference'
  }
  ruleset('rulesets/unused.xml')
}
