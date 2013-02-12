FuzzyDeduper
============

Tool for finding candidate duplicates to be reviewed manually, in Scala

Summary
-------
This is a migration of my FuzzyDeduperJava project to scala. I orginally wrote this tool in Java (which was a project requirement) but later thought it was an excellent candidate to illustrate the benefits of Scala over Java. In particular, the algorithm in FuzzyDeduper.scala is much more readable since intermediate steps are easier to label based on transformations of the data into sub-steps that often make sense contextually. With the Java project, sub-steps are often chosen based on language limitations, although there are places where this is obvious in the Scala version as well.

Problem
--------
There exists a list of names in the form of Strings, some of which are near-duplicates. We need to determine which are most likely duplicates so a human can review the list.

Solution
--------
* For each name, enumerate its subsequences ("cat" -> "c", "a", "t", "ca", "at")
* Invert this mapping so that the subsequences map to names
* Find candidate duplicates based on subsequence matches
* Score the candidates
