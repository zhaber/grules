package org.grules.scripts

import static org.grules.TestScriptEntities.PARAMETER_NAME_AUX
import static org.grules.TestScriptEntities.PARAMETER_NAME
import static org.grules.TestScriptEntities.DATE_FORMAT

import java.text.SimpleDateFormat

import org.grules.GrulesAPI
import org.grules.script.RulesScriptResult
import org.joda.time.DateTime

import spock.lang.Specification

class DateTest extends Specification {

  def "Test date functions"() {
    setup:
      def now = new DateTime()
      def dateAfter = now.plusDays(1).toDate()
      def dateBefore = now.minusDays(1).toDate()
      def dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.default)
      def parameters = [(PARAMETER_NAME):dateFormat.format(dateAfter), (PARAMETER_NAME_AUX):dateBefore]
      RulesScriptResult result = GrulesAPI.applyRules(DateGrules, parameters)
    expect:
      dateFormat.format(result.cleanParameters[PARAMETER_NAME]) == dateFormat.format(dateAfter)
      dateFormat.format(result.cleanParameters[PARAMETER_NAME_AUX]) == dateFormat.format(dateBefore)
  }

}
