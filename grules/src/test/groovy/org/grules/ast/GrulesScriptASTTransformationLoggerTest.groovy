package org.grules.ast

import org.codehaus.groovy.control.io.NullWriter

import spock.lang.Specification

class GrulesScriptASTTransformationLoggerTest extends Specification {

  def "GrulesScriptASTTransformationLoggerTest writes to null writer if log file is not writable"() {
    setup:
      GrulesASTTransformationLogger logger = new GrulesASTTransformationLogger('\0')
    expect:
      logger.writer == NullWriter.DEFAULT
  }
}
