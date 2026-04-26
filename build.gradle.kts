plugins {
    id("com.android.application") version "9.2.0" apply false
}

val externalBuildRoot = providers.gradleProperty("externalBuildRoot")
    .orElse("${System.getProperty("user.home")}/AndroidBuilds/CalculatorVault")

layout.buildDirectory.set(file("${externalBuildRoot.get()}/root"))

subprojects {
    layout.buildDirectory.set(file("${externalBuildRoot.get()}/$name"))
}
