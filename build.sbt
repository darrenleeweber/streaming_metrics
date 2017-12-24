
lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.4",
)

lazy val median_options = (project in file("median_options"))
  .settings(
    commonSettings,
    // other settings
  )

lazy val user_metrics = (project in file("user_metrics"))
  .settings(
    commonSettings,
    // other settings
  )

