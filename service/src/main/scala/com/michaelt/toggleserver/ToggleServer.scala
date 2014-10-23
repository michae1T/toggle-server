package com.michaelt.toggleserver

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import com.sun.jersey.spi.container.servlet.ServletContainer

object ToggleServer {
  def main(args: Array[String]) {
    val server = new Server(6060)
    
    val holder:ServletHolder = new ServletHolder(classOf[ServletContainer])
    holder.setInitParameter("com.sun.jersey.config.property.resourceConfigClass",
                            "com.sun.jersey.api.core.PackagesResourceConfig")
    holder.setInitParameter("com.sun.jersey.config.property.packages",
                            "com.michaelt.toggleserver.resources")
    
    val context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS)
    context.addServlet(holder, "/*")
                            
    server.start
    server.join
  }
}
