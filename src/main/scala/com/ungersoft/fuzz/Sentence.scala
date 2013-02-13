package com.ungersoft.fuzz

/**
 * Created with IntelliJ IDEA.
 * User: Randy
 * Date: 2/12/13
 * Time: 9:46 PM
 */

//implicit object OrderedSentence extends Ordering[Sentence]

case class Sentence(fullSentence: String, smallestPhonemeSize: Int) {
  lazy val strippedSentence = fullSentence.toLowerCase.replaceAll("[^a-z]","")          //remove non a-z chars
  lazy val phonemes: Set[String] = findPhonemes(Seq.empty, smallestPhonemeSize) toSet   //find all subsequences above minimum size
  val largestPhonemeSize = strippedSentence.length  //todo: Configurable

  //Find all subsequences from minimum size to word.length
  @annotation.tailrec
  private def findPhonemes(phonemeList: Seq[String], sizeToFind: Int): Seq[String] = {
    if(strippedSentence.size <= smallestPhonemeSize) Seq(strippedSentence)              //If sentence is below minimum size, just use sentence as only phoneme
    else if(sizeToFind == largestPhonemeSize) phonemeList                               //Done processing, return accumulator
    else findPhonemes(phonemeList ++ phonemesOfSize(sizeToFind), sizeToFind + 1)        //continue processing with a size one greater
  }

  //Iterate via a sliding window of size n to find subsequences of size n
  protected def phonemesOfSize(n: Int):Set[String] = strippedSentence.iterator.sliding(n, 1) map (_.mkString) toSet

  def phonemesInCommon(thatSentence: Sentence): Set[String] = phonemes intersect thatSentence.phonemes
}

