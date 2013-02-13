package com.ungersoft.fuzz

/**
 * Created with IntelliJ IDEA.
 * User: Randy
 * Date: 2/12/13
 * Time: 9:08 PM
 */

trait MatchScorer {
  def score(sentenceOne: Sentence, sentenceTwo: Sentence)(implicit phonemeMapper: PhonemeMapper): Float
}

case class MathyScorer() extends MatchScorer {

  def score(sentenceOne: Sentence, sentenceTwo: Sentence)(implicit phonemeMapper: PhonemeMapper): Float = {
    def numberOfPartners(phoneme: String) = phonemeMapper.frequencyOfPhoneme(phoneme)._1
    val onePhonemes = sentenceOne.phonemeList.phonemes
    val twoPhonemes = sentenceTwo.phonemeList.phonemes
    val sharedPhonemes = onePhonemes intersect twoPhonemes

    val points = sharedPhonemes map (phoneme => (math.pow(phoneme.length, 3) / math.pow(numberOfPartners(phoneme), .1)))
    points.sum.toFloat
//    0
  }
}

case class SimpleScorer() extends MatchScorer {
  def score(sentenceOne: Sentence, sentenceTwo: Sentence)(implicit phonemeMapper: PhonemeMapper): Float = {
    val onePhonemes = sentenceOne.phonemeList.phonemes
    val twoPhonemes = sentenceTwo.phonemeList.phonemes

    lazy val sharedPhonemes = onePhonemes intersect twoPhonemes

    lazy val percentOne = sharedPhonemes.size.toFloat / onePhonemes.size
    lazy val percentTwo = sharedPhonemes.size.toFloat / twoPhonemes.size

    percentOne * percentTwo
  }
}