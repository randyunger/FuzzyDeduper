package com.ungersoft.fuzz

/**
 * Created with IntelliJ IDEA.
 * User: Randy
 * Date: 2/12/13
 * Time: 9:08 PM
 */

trait MatchScorer {
  def score(sentenceOne: Sentence, sentenceTwo: Sentence)(implicit phonemeMapper: PhonemeMapper): Double
}

case class SelfScorer() extends MatchScorer {

  var selfScores = Map.empty[Sentence, Double]

  def selfScore(sentence: Sentence)(implicit phonemeMapper: PhonemeMapper):Double = {
    selfScores.get(sentence) match {
      case Some(score) => score
      case None => {
        val sco = score(sentence, sentence)
        selfScores = selfScores + (sentence -> sco)
        sco
      }
    }
  }

  def score(sentenceOne: Sentence, sentenceTwo: Sentence)(implicit phonemeMapper: PhonemeMapper): Double = {
    val onePhonemes = sentenceOne.phonemes
    val twoPhonemes = sentenceTwo.phonemes
    val sharedPhonemesRaw = onePhonemes intersect twoPhonemes                        //phonemes in common
    val nonsharedPhonemesRaw = (onePhonemes union twoPhonemes) diff sharedPhonemesRaw   //phonemes not in common

    val sharedPhonemes = sharedPhonemesRaw filter (phoneme => phonemeMapper.numberOfPartners(phoneme) < 10)
    val nonsharedPhonemes = nonsharedPhonemesRaw filter (phoneme => phonemeMapper.numberOfPartners(phoneme) < 10)

//    val phonemesByRarity = (phonemeMapper.phonemesToSentences map { case (k, vlist) => (k, vlist.size)}).toList sortBy (-_._2)
//    phonemesByRarity foreach println

    val ss = sharedPhonemes.size
    val ns = nonsharedPhonemes.size
    val rs =  sharedPhonemes.size.toDouble / nonsharedPhonemes.size

    if (sharedPhonemes.size.toDouble / nonsharedPhonemes.size > .1 ) {
      //proportional to rarity of shared phonemes
      //inversely proportional to rarity of non-shared phonemes
      val numer = (sharedPhonemes map (phoneme => (math.pow(phoneme.length, 1.3) ))).sum
      val denom = (nonsharedPhonemes map (phoneme => math.pow(phoneme.length, 1.5) )).sum
      val score = numer / math.max(denom, 1)  //min of 1

      if (sentenceOne == sentenceTwo) {
        score
      }
      else {
        val one = selfScore(sentenceOne)
        val two = selfScore(sentenceTwo)
        val avgSelfs = (one + two)/2
        score/avgSelfs
      }

//      val r = sharedPhonemes.size.toDouble / nonsharedPhonemes.size
//      r
    }
    else 0
  }
}

case class ScalaMathyScorer() extends MatchScorer {
  def score(sentenceOne: Sentence, sentenceTwo: Sentence)(implicit phonemeMapper: PhonemeMapper): Double = {
    val onePhonemes = sentenceOne.phonemes
    val twoPhonemes = sentenceTwo.phonemes
    val sharedPhonemesRaw = onePhonemes intersect twoPhonemes                        //phonemes in common
    val nonsharedPhonemesRaw = (onePhonemes union twoPhonemes) diff sharedPhonemesRaw   //phonemes not in common

    val sharedPhonemes = sharedPhonemesRaw filter (phoneme => phonemeMapper.numberOfPartners(phoneme) < 10)
    val nonsharedPhonemes = nonsharedPhonemesRaw filter (phoneme => phonemeMapper.numberOfPartners(phoneme) < 10)

    //    val phonemesByRarity = (phonemeMapper.phonemesToSentences map { case (k, vlist) => (k, vlist.size)}).toList sortBy (-_._2)
    //    phonemesByRarity foreach println

//    val ss = sharedPhonemes.size
//    val ns = nonsharedPhonemes.size
//    val r =  sharedPhonemes.size.toDouble / nonsharedPhonemes.size

    if (sharedPhonemes.size.toDouble / nonsharedPhonemes.size > .01 ) {
      //proportional to rarity of shared phonemes
      //inversely proportional to rarity of non-shared phonemes
      val numer = (sharedPhonemes map (phoneme => (math.pow(phoneme.length, 3) / math.pow(phonemeMapper.numberOfPartners(phoneme), .1)))).sum
      val denom = (nonsharedPhonemes map (phoneme => math.pow(phoneme.length, 5) / math.pow(phonemeMapper.numberOfPartners(phoneme), .8))).sum
      val r = numer / denom
      r
      //      val r = sharedPhonemes.size.toDouble / nonsharedPhonemes.size
      //      r
    }
    else 0
  }
}

case class MathyScorer() extends MatchScorer {

  def score(sentenceOne: Sentence, sentenceTwo: Sentence)(implicit phonemeMapper: PhonemeMapper): Double = {

    val onePhonemes = sentenceOne.phonemes
    val twoPhonemes = sentenceTwo.phonemes
    val sharedPhonemesRaw = onePhonemes intersect twoPhonemes                        //phonemes in common
    val nonsharedPhonemesRaw = (onePhonemes union twoPhonemes) diff sharedPhonemesRaw   //phonemes not in common

    val sharedPhonemes = sharedPhonemesRaw filter (phoneme => phonemeMapper.numberOfPartners(phoneme) < 10)
    val nonsharedPhonemes = nonsharedPhonemesRaw filter (phoneme => phonemeMapper.numberOfPartners(phoneme) < 10)

    val points = sharedPhonemes map (phoneme => (math.pow(phoneme.length, 3) / math.pow(phonemeMapper.numberOfPartners(phoneme), .1)))
    val discountedPoints = nonsharedPhonemes map (phoneme => math.pow(phoneme.length, 5) / math.pow(phonemeMapper.numberOfPartners(phoneme), .8))
//    println("points: " + points + " disc:" + discountedPoints)
    points.sum / discountedPoints.sum
  }
}

case class SimpleScorer() extends MatchScorer {
  def score(sentenceOne: Sentence, sentenceTwo: Sentence)(implicit phonemeMapper: PhonemeMapper): Double = {
    val onePhonemes = sentenceOne.phonemes
    val twoPhonemes = sentenceTwo.phonemes

    lazy val sharedPhonemes = onePhonemes intersect twoPhonemes

    lazy val percentOne = sharedPhonemes.size.toDouble / onePhonemes.size
    lazy val percentTwo = sharedPhonemes.size.toDouble / twoPhonemes.size

    percentOne * percentTwo
  }
}