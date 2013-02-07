//package com.ungersoft.fuzz
//
//import java.util._
//
///**
// * Created by IntelliJ IDEA.
// * User: Randy
// * Date: 9/4/12
// * Time: 8:36 PM
// */
//class FuzzyDeduper2 {
//  /**
//   * Split input into segments
//   * todo: Phonemer could be a separate class?
//   * @param size
//   * @param input
//   * @return
//   */
//  def makePhonemes(input: String, size: Int): List[String] = {
//    val phonemeList: List[String] = new ArrayList[String]
//    var i: Int = 0
//    while (i + size <= input.length) {
//      phonemeList.add(input.substring(i, i + size))
//      i += 1
//    }
//    return phonemeList
//  }
//
//  def makeAllPhonemes(input: String, startingSize: Int): List[String] = {
//    val phonemeList: List[String] = new ArrayList[String]
//    {
//      var i: Int = startingSize
//      while (i <= input.length && i <= 10) {
//        {
//          if (input.length < startingSize) phonemeList.add(input)
//          else phonemeList.addAll(makePhonemes(input, i))
//        }
//        ({
//          i += 1; i - 1
//        })
//      }
//    }
//    return phonemeList
//  }
//
//  /**
//   * Compile list of candidates
//   * @param candidateList
//   */
//  def this(candidateList: Set[String], granularity: Int) {
//    this()
//    if (candidateList == null || candidateList.size < 1) throw new IllegalArgumentException
//    this.candidates = candidateList
//    this.granularity = granularity
//    import scala.collection.JavaConversions._
//    for (candidate <- candidates) {
//      val candidate2 = stripIllegalChars(candidate)
//      val phonemes: List[String] = makeAllPhonemes(candidate2, granularity)
//      upsertPhonemes(phonemeMap, candidate2, phonemes)
//    }
//  }
//
//  private def stripIllegalChars(candidate: String): String = {
//    return candidate
//  }
//
//  private def upsertPhonemes(m: Map[String, Set[String]], candidate: String, phonemeList: List[String]) {
//    import scala.collection.JavaConversions._
//    for (phoneme <- phonemeList) {
//      if (m.containsKey(phoneme)) {
//        m.get(phoneme).add(candidate)
//      }
//      else {
//        val c: Set[String] = new HashSet[String]
//        c.add(candidate)
//        m.put(phoneme, c)
//      }
//    }
//  }
//
//  def dumpPhonemes: HashMap[String, Set[String]] = {
//    return phonemeMap
//  }
//
//  def candidatesByRank: Map[Pair[String, String], Double] = {
//    val scores: Map[Pair[String, String], Double] = new HashMap[Pair[String, String], Double]
//    import scala.collection.JavaConversions._
//    for (phoneme <- phonemeMap.keySet) {
//      val words: Set[String] = phonemeMap.get(phoneme)
//      val score: Double = scoreIt(phoneme.length, words.size)
//      if (score > 500) {
//        val wordPairs: Set[Pair[String, String]] = makePairs(words)
//        import scala.collection.JavaConversions._
//        for (p <- wordPairs) {
//          val discountedScore: Double = discountScore(score, p)
//          if (scores.containsKey(p)) {
//            scores.put(p, scores.get(p) + discountedScore)
//          }
//          else scores.put(p, discountedScore)
//        }
//      }
//    }
//    return MapUtil.sortByValue(scores)
//  }
//
//  private def makePairs(wordList: Set[String]): Set[Pair[String, String]] = {
//    val wordPairs: Set[Pair[String, String]] = new HashSet[Pair[String, String]]
//    val words: Set[String] = new HashSet[String]
//    if (wordList.size < 2 || wordList.size > 100) return wordPairs
//    words.addAll(wordList)
//    while (!words.isEmpty) {
//      val i: Iterator[_] = words.iterator
//      val word: String = i.next.asInstanceOf[String]
//      words.remove(word)
//      import scala.collection.JavaConversions._
//      for (otherWord <- words) {
//        wordPairs.add(new Pair[String, String](word, otherWord))
//      }
//    }
//    return wordPairs
//  }
//
//  private def scoreIt(phonemeLength: Int, numberOfPartners: Int): Double = {
//    return Math.pow(phonemeLength, 3) / Math.pow(numberOfPartners.asInstanceOf[Double], (.1))
//  }
//
//  private def discountScore(score: Double, p: Pair[String, String]): Double = {
//    val pairPhonemeMap: HashMap[String, Set[String]] = new HashMap[String, Set[String]]
//    upsertPhonemes(pairPhonemeMap, p.getLeft, makeAllPhonemes(p.getLeft, this.granularity))
//    upsertPhonemes(pairPhonemeMap, p.getRight, makeAllPhonemes(p.getRight, this.granularity))
//    var accScore: Double = 0
//    import scala.collection.JavaConversions._
//    for (entry <- pairPhonemeMap.entrySet) {
//      if (entry.getValue.size == 1) {
//        accScore += dScore(entry.getKey.length, phonemeMap.get(entry.getKey).size)
//      }
//    }
//    return score / accScore
//  }
//
//  private def dScore(phonemeLength: Int, numberOfPartners: Int): Double = {
//    val s: Double = Math.pow(phonemeLength, 5) / Math.pow(numberOfPartners.asInstanceOf[Double], (.8))
//    return s
//  }
//
//  private var candidates: Set[String] = null
//  private var phonemeMap: HashMap[String, Set[String]] = new HashMap[String, Set[String]]
//  private var granularity: Int = 0
//}