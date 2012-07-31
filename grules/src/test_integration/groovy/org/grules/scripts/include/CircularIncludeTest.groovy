package org.grules.scripts.include

import org.grules.GrulesAPI
import org.grules.script.CircularIncludeException

import spock.lang.Specification

class CircularIncludeTest extends Specification {

  def "Circular test"() {
    when:
      GrulesAPI.applyRules(CircularIncludeGrules, [:])
    then:
      thrown(CircularIncludeException)
  }

}
