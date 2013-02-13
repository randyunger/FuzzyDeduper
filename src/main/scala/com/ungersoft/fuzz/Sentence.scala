package com.ungersoft.fuzz

/**
 * Created with IntelliJ IDEA.
 * User: Randy
 * Date: 2/12/13
 * Time: 9:46 PM
 */

//implicit object OrderedSentence extends Ordering[Sentence]

case class Sentence(fullSentence: String, smallestPhonemeSize: Int) {
  lazy val strippedSentence = fullSentence.toLowerCase.replaceAll("[^a-z]","")
  lazy val phonemeList = PhonemeList(strippedSentence, smallestPhonemeSize)

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

