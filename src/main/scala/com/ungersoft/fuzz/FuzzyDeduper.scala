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
class FuzzyDeduper(sentenceList: Iterable[String], smallestPhonemeSize: Int = 5) {

  //map each input string into a Sentence containing a list of its phonemes
//  val sentenceList:Iterable[String] = List("a first sentence", "a second sentence", "a first another second")

  lazy val processedSentences: Iterable[(String, Sentence)] =
    sentenceList map (sentenceString => (sentenceString -> Sentence(sentenceString, smallestPhonemeSize)))

  //Invert pairings (phonemes to sentences instead of sentences to phonemes)
  lazy val phonemesSentencePairs: Iterable[(String, Sentence)] = for {
    (fullSentence, processedSentence) <- processedSentences
    phoneme <- processedSentence.phonemeList.phonemes
  } yield phoneme -> processedSentence

//  lazy val allPhonemes = phonemesSentencePairs map(_._1) //{ case (key, value) => key}

  //Convert list to map keyed off of phonemes, and remove phonemes that appear only in one sentence
  lazy val phonemesToSentencesWithDupeKey = phonemesSentencePairs groupBy (_._1) filter { case (key, valueList) => valueList.size > 1 }

  //Entries now look like "subseq -> (subseq, List(Sentence(subseq)))". Map to remove the dupe subseq //: Map[String, Set[Sentence]]
  lazy val phonemesToSentences = phonemesToSentencesWithDupeKey map { case(k,v) => k -> { v map { case(a,b) => b } }.toSet }


  def candidatesByRank: Iterable[String] = {
    Nil
  }

  def sentencesThatSharePhonemesWith(sentence: Sentence): Set[Sentence] = phonemesToSentences.
    filter { case (key, vlist) => vlist contains sentence }.      //Find all sentences that share phonemes with sentence
    map { case (key, vlist) => key -> (vlist - sentence) }.        //Remove sentence from list
    values.toSet.flatten

  def phonemesSharedBetween(sentenceOne: Sentence, sentenceTwo: Sentence): Set[String] = phonemesToSentences.
    filter { case (k, vlist) => vlist.contains(sentenceOne) && vlist.contains(sentenceTwo) }.
    keys

  def phonemeRarity(phoneme: String): (Int, Int) = (phonemesToSentences.get(phoneme) map (_.size), phonemesToSentences.size)
}

case class MatchScorer(sentenceOne: Sentence, sentenceTwo: Sentence) {

}

case class Sentence(fullSentence: String, smallestPhonemeSize: Int) {
  lazy val strippedSentence = fullSentence.replaceAll(" ","")
  lazy val phonemeList = PhonemeList(strippedSentence, smallestPhonemeSize)

  def scoreVersus(thatSentence: Sentence): Long = {
    val ourPhonemes = phonemeList.phonemes
    val theirPhonemes = thatSentence.phonemeList.phonemes
    val sharedPhonemes = ourPhonemes.intersect(theirPhonemes)

    val ourPercent = sharedPhonemes.size.toFloat / ourPhonemes.size             //Percent of us contained in them
    val theirPercent = sharedPhonemes.size.toFloat / theirPhonemes.size         //Percent of them contained in us

    ourPercent * theirPercent
  }
}

case class PhonemeList(words: String, smallestPhonemeSize: Int) {
  private val largestPhonemeSize = words.length  //todo: Configurable?

  lazy val phonemes: Seq[String] = findPhonemes(Seq.empty, smallestPhonemeSize)

  @annotation.tailrec
  private def findPhonemes(phonemeList: Seq[String], size: Int): Seq[String] = {
    if(size == largestPhonemeSize) phonemeList
    else findPhonemes(phonemeList ++ phonemesOfSize(size), size + 1)
  }

  private def phonemesOfSize(n: Int):Set[String] = words.iterator.sliding(n, 1) map (_.mkString) toSet  //Iterate via a sliding window of size n
}
