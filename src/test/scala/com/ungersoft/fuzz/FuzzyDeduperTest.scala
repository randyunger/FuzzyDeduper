package com.ungersoft.fuzz

import org.specs2.mutable.Specification

/**
 * Created with IntelliJ IDEA.
 * User: randy
 * Date: 2/11/13
 * Time: 9:15 AM
 */
class FuzzyDeduperTest extends Specification {
  "Fuzzy Deduper" should {
    "exist" in {
      new PhonemeMapper(Seq("a"), 1)
      1 must be equalTo(1)
    }
  }
}
