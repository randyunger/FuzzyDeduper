package com.ungersoft.fuzz

/**
 * Created with IntelliJ IDEA.
 * User: Randy
 * Date: 2/6/13
 * Time: 7:35 PM
 */

trait Reader{
  def items: List[String]
}

object Reader {
  def apply(fileName: String)() = {}
}

class FileReader(fileName: String) extends Reader {
  def items = scala.io.Source.fromFile("myfile.txt").getLines().toList
}
