package com.michaelt.toggleserver.helper

object JQuery {

  lazy val source = {
    val stream = getClass.getResourceAsStream("/jquery-1.11.0.min.js")
    Option(stream).map { s =>
      scala.io.Source.fromInputStream(s).getLines().mkString("\n")
    }
  }

}
