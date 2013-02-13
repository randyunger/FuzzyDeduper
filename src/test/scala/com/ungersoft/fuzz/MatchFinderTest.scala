package com.ungersoft.fuzz

import org.specs2.mutable.Specification
import org.specs2.specification.Scope

/**
 * Created with IntelliJ IDEA.
 * User: Randy
 * Date: 2/12/13
 * Time: 7:35 PM
 */
class MatchFinderTest extends Specification {

  trait DeduperSetup extends Scope {
//    val lines = scala.io.Source.fromFile("subset.txt")("UTF-8").getLines().toSet
    val lines = List("Sony Motion Pictures", "Sony Inc.", "Paramount Pictures")
    implicit val pm = new PhonemeMapper(lines, 5)
//    val scorer = new MatchScorer()
  }

  "MatchFinder" should {
    "exist" in new DeduperSetup {
      new MatchFinder()(pm, SimpleScorer())
    }

    "score matches" in new DeduperSetup {
      val mf = new MatchFinder()(pm, SimpleScorer())
      mf.scores foreach println
      println("head:" + mf.scores.head)// must b
      mf.scores must haveKey(Sentence("Sony Motion Pictures",5)) //-> List((Sentence("Paramount Pictures",5),0.010683761)))
    }

    "rank matches" in new DeduperSetup {
      val mf = new MatchFinder()(pm, SimpleScorer())
      val topTwo = mf.rankedMatches.take(2)
      topTwo.head._3 must be_>= (topTwo.last._3)
    }
  }

}
