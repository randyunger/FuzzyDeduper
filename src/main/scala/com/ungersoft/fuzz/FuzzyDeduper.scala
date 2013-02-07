package com.ungersoft.fuzz

import scalaz._
import Scalaz._
import annotation.tailrec
import collection.mutable

/**
 * Created with IntelliJ IDEA.
 * User: Randy
 * Date: 2/6/13
 * Time: 7:58 PM
 */

class FuzzyDeduper(sentenceList: Set[String], smallestPhonemeSize: Int = 5) {

//  val phonemeList = sentenceList map (sentence => PhonemeList(sentence, smallestPhonemeSize).phonemes)
  val phonemesToSentences: Map[String, Set[String]] = mutable.Map.empty




  def candidatesByRank: List[String] = {
    Nil
  }

}
case class Sentence(words: String) {
  lazy val phonemeList = Nil
  lazy val strippedSentence = ""
}

case class PhonemeList(sentence: String, smallestPhonemeSize: Int) {
  lazy val phonemes = findPhonemes(Nil, smallestPhonemeSize)

  @tailrec
  private def findPhonemes(phonemeList: List[List[String]], size: Int): List[String] = {
    if(size == sentence.length) phonemeList
    else findPhonemes(phonemeList :: phonemesOfSize(size), size + 1)
  }

  private def phonemesOfSize(n: Int) = sentence.toList.iterator.sliding(n, 1)
}
