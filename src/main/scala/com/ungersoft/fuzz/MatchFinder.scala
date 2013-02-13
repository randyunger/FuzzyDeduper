package com.ungersoft.fuzz

/**
 * Created with IntelliJ IDEA.
 * User: Randy
 * Date: 2/12/13
 * Time: 9:48 PM
 */

case class MatchFinder(threshhold: Double = 0.00001)(implicit phonemeMapper: PhonemeMapper, scorer: MatchScorer) {
  val sampleSize = Integer.MAX_VALUE

  lazy val scores = phonemeMapper.processedSentences.
    take(sampleSize).
    map (sentence => sentence._2 -> (phonemeMapper.bestMatchesFor(sentence._2)(scorer) filter (_._2 > threshhold))).
    filter { case (key, vlist) => !vlist.isEmpty }

  lazy val matchesWithScores = for {
    (sentenceOne, hitsList) <- scores
    (sentenceTwo, score) <- hitsList
    sortedSentences = List(sentenceOne, sentenceTwo).sortBy(_.fullSentence)
  } yield (sortedSentences.head, sortedSentences.last, score)

  lazy val rankedMatches = matchesWithScores.toList sortBy (-_._3)
}
