package com.ungersoft.fuzz

import scalaz._
import Scalaz._

/**
 * Created with IntelliJ IDEA.
 * User: Randy
 * Date: 2/6/13
 * Time: 7:58 PM
 */

                                                     //todo: Move this config
case class PhonemeMapper(sentenceList: Iterable[String], smallestPhonemeSize: Int = 5) {
                                       //Uses Iterable for .sliding
  //map each input string into a Sentence containing a list of its phonemes
  lazy val processedSentences: Iterable[(String, Sentence)] =
    sentenceList map (sentenceString => (sentenceString -> Sentence(sentenceString, smallestPhonemeSize)))

  //Invert pairings (phonemes to sentences instead of sentences to phonemes)
  lazy val phonemesSentencePairs: Iterable[(String, Sentence)] = for {
    (fullSentence, processedSentence) <- processedSentences
    phoneme <- processedSentence.phonemes
  } yield phoneme -> processedSentence

  lazy val phonemesToSentences = phonemesSentencePairs.toList.
    groupBy (_._1).                                                  //Convert list to map keyed off of phonemes
    filter { case (key, valueList) => valueList.size > 1             //remove phonemes that appear only in one sentence
    } |> removeDupeKeyFromPair                                       //remove duplicate key, Artifact of groupBy: abc -> List[(abc, Sentence)] becomes abc -> List[Sentence]

  def sentencesThatSharePhonemesWith(sentence: Sentence): Set[Sentence] = phonemesToSentences.
    filter { case (key, vlist) => vlist contains sentence }.      //Find all sentences that share phonemes with sentence
    map { case (key, vlist) => key -> (vlist - sentence) }.        //Remove sentence from list
    values.toSet.flatten

  lazy val phonemesByFrequency = phonemesToSentences mapValues(_.size)  //Number of sentences that share this phoneme

  def numberOfPartners(phoneme: String) = phonemesByFrequency.get(phoneme).getOrElse(1) //Cannot be 0 (for division)

  //Keep count of the progress (just for monitoring progress)
  var bmfCount = 0                                                     //todo: generalize to Numeric?
  def bestMatchesFor(sentence: Sentence)(implicit scorer: MatchScorer): List[(Sentence, Double)] = {
    bmfCount += 1
    println("Scoring: " + bmfCount + ": " + sentence)
    val candidates= sentencesThatSharePhonemesWith(sentence)
    val scoreList = candidates.toList map (candidate => (candidate, scorer.score(sentence, candidate)(this)))
    scoreList sortBy (-_._2)
  }

  //Todo, generalize from List to Iterable
  //def removeDupeKeyFromPair[A, B, C[Pair[A,B]]<:Iterable[Pair[A,B]]](m: Map[A, C[Pair[A,B]]]): Map[A, C[B]] =
  def removeDupeKeyFromPair[A,B](m: Map[A, List[Pair[A,B]]]): Map[A, List[B]] =
    m map { case(k, vlist) => k -> { vlist map { case(dupe, b) => b } } }
}