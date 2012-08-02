package org.grules.script.expressions

class SubrulesSeqWrapper {

  /**
   * This method overloads other wrapper methods and immediately returns the given subrules sequence.
   *
   * @param subrulesSeq subrules sequence
   * @return the same term
   */
  static SubrulesSeq wrap(SubrulesSeq subrulesSeq) {
    subrulesSeq
  }

  /**
   * Wraps a closure into a subrules sequence.
   *
   * @param closure a validation closure
   */
  static SubrulesSeq wrap(Closure closure) {
    SubrulesSeq subrulesSeq = new SubrulesSeq()
    subrulesSeq.add(closure)
    subrulesSeq
  }

  /**
   * Wraps a skip subrule into a subrules sequence.
   *
   * @param closure a validation closure
   */
  static SubrulesSeq wrap(Skip skip) {
    SubrulesSeq subrulesSeq = new SubrulesSeq()
    skip.functions.each { String function ->
      subrulesSeq.addSkipFunction(function)
    }
    subrulesSeq
  }

  /**
   * Wraps a subrule into a subrules sequence.
   *
   * @param subrule a subrule
   */
  static SubrulesSeq wrap(Subrule subrule) {
    SubrulesSeq subrulesSeq = new SubrulesSeq()
    subrulesSeq.add(subrule)
    subrulesSeq
  }

  /**
   * Wraps a subrules into a subrules sequence.
   *
   * @param subrules subrules
   */
  static SubrulesSeq wrap(List<Subrule> subrules) {
    SubrulesSeq subrulesSeq = new SubrulesSeq()
    subrules.each { Subrule subrule ->
      subrulesSeq.add(subrule)
    }
    subrulesSeq
  }


  /**
   * Wraps a term into a subrules sequence.
   *
   * @param conversion or validation term
   */
  static SubrulesSeq wrap(Term term) {
    SubrulesSeq subrulesSeq = new SubrulesSeq()
    subrulesSeq.add(term)
    subrulesSeq
  }

  /**
  * An unexpected subrules sequence.
  *
  * @param expression subrules sequence expression
  * @return throws the InvalidTermException exception
  */
  static SubrulesSeq wrap(expression) {
    throw new InvalidSubrulesSeqException(expression)
  }
}
