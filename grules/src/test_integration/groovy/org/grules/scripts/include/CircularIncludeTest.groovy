package org.grules.scripts.include

import org.grules.Grules
import org.grules.script.CircularIncludeException

import spock.lang.Specification

class CircularIncludeTest extends Specification {

  def "Circular test"() {
    when:
      Grules.applyRules(CircularIncludeGrules, [:])
    then:
      thrown(CircularIncludeException)
  }

}
