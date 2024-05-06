plugins {
  id("org.siouan.frontend-jdk17") version "8.0.0"
}

frontend {
  nodeVersion.set("21.7.2")
  assembleScript.set("run build")
  cleanScript.set("run clean")
  //checkScript.set("run test")
  verboseModeEnabled.set(true)
}
