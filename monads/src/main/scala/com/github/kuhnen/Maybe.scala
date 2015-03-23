package com.github.kuhnen

/**
 * Created by kuhnen on 3/23/15.
 */

//Definition of Monad
//A monad is defined in the wikibook, and in Wikipedia, as having three parts:
//  A type constructor
//  A unit function, commonly called return in Haskell
//  A binding operation, commonly called >>= in Haskell
//  How do these elements translate into Scala? Let's examine them one by one.



sealed trait Maybe[+A] {

  def flatMap[B](f: A => Maybe[B]): Maybe[B]

}

case object MaybeNot extends Maybe[Nothing] {

  override def flatMap[B](f: Nothing => Maybe[B]) = MaybeNot
}


case class Just[+A](a: A) extends Maybe[A] {

  override def flatMap[B](f: A => Maybe[B]) = f(a)

}
