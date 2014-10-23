import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._
import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._
import com.typesafe.sbt.packager.linux.LinuxPackageMapping
import com.typesafe.sbt.packager.archetypes.ServerLoader.Systemd

object ToggleServer extends Build { 
  lazy val buildSettings = Seq(
    version := "0.3",
    organization := "com.michaelt",
    scalaVersion := "2.11.1"
  ) ++ packageArchetype.java_application

  val toggleserver = (project in file("service")).
    settings(buildSettings: _*).
    settings(assemblySettings: _*).
    settings(
      maintainer in Linux := "Michael T <michaelT@live.ca>",
      packageSummary in Linux := "Silly scala HTTP system toggler",
      packageDescription in Linux := """Provides a primitive webservice and 
                               REST framework to externally control
                               system services like a VPN""",
      rpmVendor := "MichaelT",
      rpmLicense := Some("GPLv3"),
      daemonUser in Linux := "nobody",
      daemonGroup in Linux := "nobody",
      linuxPackageSymlinks := Seq.empty,
      defaultLinuxInstallLocation := "/opt",
      rpmPrefix := Some(defaultLinuxInstallLocation.value),
      rpmPost := Some(post),
      rpmPreun := Some(preun)
    ).settings(
      jarName in assembly := "toggleserver.jar",
      mainClass in assembly := Some("com.michaelt.toggleserver.ToggleServer"),
      libraryDependencies ++= Seq(
        "org.eclipse.jetty" % "jetty-server" % "9.1.0.M0",
        "org.eclipse.jetty" % "jetty-servlet" % "9.1.0.M0",
        "com.sun.jersey" % "jersey-core" % "1.17.1",
        "com.sun.jersey" % "jersey-server" % "1.17.1",
        "com.sun.jersey" % "jersey-servlet" % "1.17.1"
      )
    )

  val post = """
#!/bin/bash

RUN_FILE=/usr/lib/systemd/system/toggleserver.service

cat > $RUN_FILE << EOF
[Unit]
Description=Interface to touch tmp files via http
After=syslog.target local-fs.target network.target

[Service]
User=nobody
Group=nobody
Type=simple
ExecStart=/opt/toggleserver/bin/toggleserver

[Install]
WantedBy=multi-user.target
EOF

chown root:root $RUN_FILE
chmod 644 $RUN_FILE

systemctl daemon-reload
systemctl enable toggleserver.service
systemctl restart toggleserver.service
"""

  val preun = """
#!/bin/bash

systemctl stop toggleserver.service
rm /usr/lib/systemd/system/toggleserver.service
systemctl daemon-reload
"""
}

