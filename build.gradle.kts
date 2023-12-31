plugins {
  alias(libs.plugins.kotlin.jvm)
  id("java-gradle-plugin")
  alias(libs.plugins.kotlin.dsl)
  alias(libs.plugins.spotless)
  alias(libs.plugins.versionCatalogUpdate)
  alias(libs.plugins.gradle.publish)
}

group = "nl.littlerobots"

version = "1.0.0"

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

kotlin { jvmToolchain(17) }

gradlePlugin {
  website = "https://github.com/littlerobots/android-basic-conventions-gradle-plugin"
  vcsUrl = "git@github.com:littlerobots/android-basic-conventions-gradle-plugin.git"

  plugins {
    register("basicAndroidConventions") {
      id = "nl.littlerobots.android-basic-conventions"
      implementationClass = "nl.littlerobots.abc.plugin.AndroidBasicConventionsPlugin"
      displayName = "Sets up basic conventions for Android projects"
      description =
          "Sets up basic conventions for Android projects, such as target sdk and Kotlin/Java versions"
      tags = listOf("Android", "conventions")
    }
  }
}

spotless {
  kotlin {
    target("**/*.kt")
    ktfmt()
    licenseHeaderFile(file("spotless/license.txt"))
  }

  kotlinGradle { ktfmt() }
}

dependencies {
  compileOnly(libs.android.gradle.api)
  compileOnly(libs.kotlin.gradle.plugin)
}
