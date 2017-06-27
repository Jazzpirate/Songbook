package Songbook

import utils._

/**
  * This package defines various MMT-independent high-level APIs.
  */
package object utils {
   /** converts a string to an integer, returns None on format exception */
   def stringToInt(s: String) = try {Some(s.toInt)} catch {case _: Exception => None}
  
   /** splits a string at a separator (returns Nil for the empty string) */
   def stringToList(s: String, sep: String = "\\s") = s.split(sep).toList match {
      case List("") => Nil
      case l => l
   }
   
   /** string index modulo string length */
   def moduloLength(s:String, i: Int) = {
     val m = i % s.length
     // fix falsely negative results
     if (m >= 0) i else i+s.length
   }
   /** substring of a string given by begin and end, negative indices allowed */
   def substringFromTo(s: String, from: Int, to: Int) = {
     s.substring(moduloLength(s,from), moduloLength(s,to))
   }
   /** substring of a string given by begin and length, negative index allowed */
   def substringFrom(s: String, from: Int, length: Int) = substringFromTo(s, from, from+length)
   
   /** splits a string at whitespace, quoted segments may contain whitespace, \" for quote, ignores leading/trailing whitespace */
   def splitAtWhitespace(s: String): List[String] = {
      var segments : List[String] = Nil
      var current = ""
      var inquoted = false
      var todo = s
      def done {
        if (current.nonEmpty) {
          segments ::= current
          current = ""
        }
      }
      while (todo.nonEmpty) {
        if (todo.startsWith("\\\"")) {
          current += '"'
          todo = todo.substring(2)
        } else {
          val c = todo(0)
          todo  = todo.substring(1)
          if (inquoted) {
            if (c == '"') {
              done
              inquoted = false
            } else {
              current += c
            }
          } else {
            if (c == '"') {
              inquoted = true
            } else if (c.isWhitespace) {
              done
            } else {
              current += c
            }
          }
        }
      }
      done
      segments.reverse
   }
   
   /** turns a list into a string  by inserting a separator */
   def listToString[A](l: List[A], sep: String) = l.map(_.toString).mkString(sep)
   
   /** repeats a strings a number of times, optionally with a separator */
   def repeatString(s: String, n: Int, sep: String = "") = Range(0,n).map(_ => s).mkString(sep)

   /** inserts a separator element in between all elements of a list */
   def insertSep[A](l: List[A], sep: A) = if (l.isEmpty) Nil else l.flatMap(a => List(sep,a)).tail
   
   /** applies a list of pairs seen as a map */
   def listmap[A,B](l: List[(A,B)], a: A): Option[B] = l.find(_._1 == a).map(_._2)
   
   /** disjointness of two lists */
   def disjoint[A](l: List[A], m: List[A]) = l.forall(a => ! m.contains(a))
   
   /** variant of fold such that associate(List(a), unit)(comp) = a instead of comp(unit, a) */
   def associate[A](l: List[A], unit: A)(comp: (A,A) => A): A = l match {
     case Nil => unit
     case hd::tl => tl.fold(hd)(comp)
   }

   /** slurps an entire stream into a string */
   def readFullStream(is: java.io.InputStream) = scala.io.Source.fromInputStream(is, "UTF-8").mkString
   
   /** a cast function that allows only casting into a subtype and returns None if the cast fails */  
   def downcast[A, B<:A](cls: Class[B])(a: A): Option[B] = a match {
     case b: B@unchecked if cls.isInstance(b) => Some(b)
     case _ => None
   }
}
