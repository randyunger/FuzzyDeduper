package com.ungersoft.fuzz

import scalaz._
import Scalaz._

/**
 * Created with IntelliJ IDEA.
 * User: Randy
 * Date: 2/6/13
 * Time: 7:58 PM
 */

//Use Iterable for .sliding
case class PhonemeMapper(sentenceList: Iterable[String], smallestPhonemeSize: Int = 5) {
//  val sentenceList:Iterable[String] = List("a first sentence", "a second sentence", "a first another second")

  //map each input string into a Sentence containing a list of its phonemes
  lazy val processedSentences: Iterable[(String, Sentence)] =
    sentenceList map (sentenceString => (sentenceString -> Sentence(sentenceString, smallestPhonemeSize)))

  //Invert pairings (phonemes to sentences instead of sentences to phonemes)
  lazy val phonemesSentencePairs: Iterable[(String, Sentence)] = for {
    (fullSentence, processedSentence) <- processedSentences
    phoneme <- processedSentence.phonemeList.phonemes
  } yield phoneme -> processedSentence

  //Convert list to map keyed off of phonemes, and remove phonemes that appear only in one sentence
  //private
  lazy val phonemesToSentencesWithDupeKey = phonemesSentencePairs.toList groupBy (_._1) filter { case (key, valueList) => valueList.size > 1 }

  //Entries now look like "subseq -> (subseq, List(Sentence(subseq)))" due to use of groupBy.
  // Now map to remove the dupe subseq //: Map[String, Set[Sentence]]
  lazy val phonemesToSentences = removeDupeKeyFromMap(phonemesToSentencesWithDupeKey)

  def sentencesThatSharePhonemesWith(sentence: Sentence): Set[Sentence] = phonemesToSentences.
    filter { case (key, vlist) => vlist contains sentence }.      //Find all sentences that share phonemes with sentence
    map { case (key, vlist) => key -> (vlist - sentence) }.        //Remove sentence from list
    values.toSet.flatten

  def phonemeRarity(phoneme: String): (Int, Int) = (phonemesToSentences.get(phoneme) map (_.size) getOrElse(0), phonemesToSentences.size)

  def candidatesByRank: Iterable[String] = {
    Nil
  }

  def bestMatchesFor(sentence: Sentence): List[(Sentence, Float)] = {
    val candidates= sentencesThatSharePhonemesWith(sentence)
    val scoreList = candidates.toList map (candidate => (candidate, sentence.scoreVersus(candidate)))
    scoreList sortBy (-_._2)
  }

  //Todo, genericize from List to Iterable
  def removeDupeKeyFromMap[A,B](m: Map[A, List[Pair[A,B]]]) = m map { case(k,v) => k -> { v map { case(a,b) => b } } }
}

case class MatchScorer(sentenceOne: Sentence, sentenceTwo: Sentence)(implicit phonemeMapper: PhonemeMapper) {

}

case class MatchFinder(scorer: MatchScorer, threshhold: Float = 0.01f)(implicit phonemeMapper: PhonemeMapper) {
  lazy val sentence = phonemeMapper.processedSentences.take(11).last._2
  val sampleSize = 50

  phonemeMapper.processedSentences.
    take(sampleSize).
    map (sentence => sentence._2 -> (phonemeMapper.bestMatchesFor(sentence._2) filter (_._2 > threshhold))).
    filter { case (key, vlist) => !vlist.isEmpty }.
    toMap


}

case class Sentence(fullSentence: String, smallestPhonemeSize: Int) {
  lazy val strippedSentence = fullSentence.toLowerCase.replaceAll("[^a-z]","")
  lazy val phonemeList = PhonemeList(strippedSentence, smallestPhonemeSize)

  def scoreVersus(thatSentence: Sentence): Float = {
    val ourPhonemes = phonemeList.phonemes
    val theirPhonemes = thatSentence.phonemeList.phonemes
    val sharedPhonemes = ourPhonemes intersect theirPhonemes

    val ourPercent = sharedPhonemes.size.toFloat / ourPhonemes.size             //Percent of us contained in them
    val theirPercent = sharedPhonemes.size.toFloat / theirPhonemes.size         //Percent of them contained in us

    ourPercent * theirPercent
  }

  def phonemesInCommon(thatSentence: Sentence): Set[String] = phonemeList.phonemes intersect thatSentence.phonemeList.phonemes

}

case class PhonemeList(words: String, smallestPhonemeSize: Int) {
  protected val largestPhonemeSize = words.length  //todo: Configurable?

  lazy val phonemes: Set[String] = findPhonemes(Seq.empty, smallestPhonemeSize) toSet //math.min(words.size - 1, smallestPhonemeSize)) toSet

  @annotation.tailrec
  private def findPhonemes(phonemeList: Seq[String], sizeToFind: Int): Seq[String] = {
    if(words.size <= smallestPhonemeSize) Seq(words)
    else if(sizeToFind == largestPhonemeSize) phonemeList
    else findPhonemes(phonemeList ++ phonemesOfSize(sizeToFind), sizeToFind + 1)
  }

  protected def phonemesOfSize(n: Int):Set[String] = words.iterator.sliding(n, 1) map (_.mkString) toSet  //Iterate via a sliding window of size n
}
