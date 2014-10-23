package com.michaelt.toggleserver.resources

import javax.ws.rs.PUT
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.QueryParam
import javax.ws.rs.PathParam
import java.io.File
import scala.io.Source

@Path("/toggle")
@Produces(Array(MediaType.TEXT_PLAIN))
class ToggleHandler {

  private lazy val togglePath = {
    val f = new File("/tmp/toggles")
    f.mkdir
    f
  }
  private lazy val chars = "0123456789abcdefghijzlmnopqrstuvwxyz-_".toSet

  @PUT
  @Path("/{type}")
  def setToggle(@PathParam("type") toggleType : String, data : String) = {
    try {
      getToggle(toggleType).map { toggle =>
        if (toggle.content == data) {
          "already toggled"
        }
        else {
          toggle.write(data)
          "toggled!"
        }
      }.getOrElse("invalid")
    } catch {
      case e:Throwable => "unknown error"
    }
  }

  @GET
  @Path("/{type}")
  def readToggle(@PathParam("type") toggleType : String) = {
    try {
      getToggle(toggleType).map(_.content).getOrElse("invalid")
    } catch {
      case e:Throwable => "unknown error"
    }
  }


  private def getToggle(toggleType : String) = {
    if (!toggleType.forall(chars.contains)) {
      None
    } else {
      Some(Toggle(toggleType))
    }
  }

  case class Toggle(toggleType : String) {
    private lazy val file = {
      val f = new File(togglePath, toggleType)
      if (!f.exists) f.createNewFile
      f
    }

    lazy val content = Source.fromFile(file).getLines.mkString

    def write(data : String) {
      val pw = new java.io.PrintWriter(file)
      try pw.write(data) finally pw.close()
    }
  }
}

