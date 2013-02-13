package com.ungersoft.fuzz

import java.util.HashSet
import java.util.Iterator
import java.util.Map

/**
 * Created by IntelliJ IDEA.
 * User: Randy
 * Date: 9/4/12
 * Time: 7:55 PM
 * To change this template use File | Settings | File Templates.
 */

object Main {
  def main(args: Array[String]) {

    val startTime: Long = System.currentTimeMillis

    //Read newline separated lines from input file
    val lines = scala.io.Source.fromFile("subset.txt")("UTF-8").getLines().toSet
//    val lines = scala.io.Source.fromFile("input.csv")("UTF-8").getLines().toSet

    val scorer = SelfScorer()
    val pm = new PhonemeMapper(lines, 5)

    val mf = new MatchFinder()(pm, scorer)

    mf.rankedMatches.take(100) foreach println
//    phonemeMapper.phonemesToSentences foreach println


//    fuzzyDeduper.candidatesByRank.take(50).foreach(println)

//    var i: Int = 0
////    val ranks: Map[Pair[String, String], Double] = fd.candidatesByRank
//    val it: Iterator[_] = ranks.keySet.iterator
    //    while (it.hasNext && i < 300) {
    //      i += 1
    //      val p: Pair[_, _] = it.next.asInstanceOf[Pair[_, _]]
    //      System.out.println("(" + i + ")  " + ranks.get(p) + "   " + p.getLeft + " == " + p.getRight)
    //    }
    val endTime: Long = System.currentTimeMillis
    System.out.println("Runtime: " + (endTime - startTime) + "ms")
  }
}