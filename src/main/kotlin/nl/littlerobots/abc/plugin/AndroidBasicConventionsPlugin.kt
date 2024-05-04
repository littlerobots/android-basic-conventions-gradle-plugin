/*
* Copyright 2023 Hugo Visser
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package nl.littlerobots.abc.plugin

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import kotlin.jvm.optionals.getOrNull
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class AndroidBasicConventionsPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    if (target == target.rootProject) {
      setupExtension(target)
    } else {
      configureProject(target)
    }
  }

  private fun configureProject(target: Project) {
    // TODO could potentially just use defaults but not sure if that makes any sense
    val extension =
        target.rootProject.extensions.findByType<AndroidBasicConventionsExtension>()
            ?: throw IllegalStateException(
                "Android basic conventions plugin is not applied to the root project")
    configureAndroid(target, extension)
    configureJvm(target, extension)
    configureKotlin(target, extension)
  }

  private fun configureKotlin(target: Project, extension: AndroidBasicConventionsExtension) {
    val jvmTarget = extension.jvmTargetWithDefault
    if (target.extensions.findByType<KotlinJvmProjectExtension>() != null) {
      target.extensions.configure<KotlinJvmProjectExtension> {
        compilerOptions {
          this.jvmTarget.set(JvmTarget.fromTarget(jvmTarget.majorVersion))
          freeCompilerArgs.add("-Xjdk-release=${jvmTarget.toJvmTarget.target}")
        }
      }
    }
    if (target.extensions.findByName("kotlinOptions") != null) {
      target.extensions.configure<KotlinJvmOptions>("kotlinOptions") {
        freeCompilerArgs += extension.kotlinOptions.freeCompilerArgs
        this.jvmTarget = jvmTarget.toJvmTarget.target
      }
    }
  }

  private fun configureJvm(target: Project, extension: AndroidBasicConventionsExtension) {
    if (target.extensions.findByType<JavaPluginExtension>() != null) {
      target.plugins.withId("java") {
        target.extensions.configure<JavaPluginExtension> {
          targetCompatibility = extension.jvmTargetWithDefault
          sourceCompatibility = extension.jvmTargetWithDefault
        }
      }
    }
  }

  private fun configureAndroid(target: Project, extension: AndroidBasicConventionsExtension) {
    target.plugins.withId("com.android.application") {
      target.extensions.configure<ApplicationExtension> {
        configureAndroidExtension(target, this, extension)
      }
    }
    target.plugins.withId("com.android.library") {
      target.extensions.configure<LibraryExtension> {
        configureAndroidExtension(target, this, extension)
      }
    }
  }

  private fun configureAndroidExtension(
      target: Project,
      android: CommonExtension<*, *, *, *, *>,
      extension: AndroidBasicConventionsExtension
  ) {
    val composeCompilerVersion =
        target.extensions
            .getByType<VersionCatalogsExtension>()
            .find("libs")
            .getOrNull()
            ?.findVersion("compose-compiler")

    val jvmTarget = extension.jvmTargetWithDefault

    with(android) {
      compileSdk = extension.compileSdk.orNull
      defaultConfig {
        if (this is ApplicationDefaultConfig) {
          targetSdk = extension.targetSdk.orNull
        }
        minSdk = extension.minSdk.orNull
      }

      composeCompilerVersion?.getOrNull()?.let {
        target.logger.debug("Configuring compose compiler version: {}", it)
        composeOptions.kotlinCompilerExtensionVersion = it.toString()
      }

      (android as ExtensionAware).extensions.configure<KotlinJvmOptions>("kotlinOptions") {
        freeCompilerArgs += extension.kotlinOptions.freeCompilerArgs
        // there must be a better way?
        this.jvmTarget = jvmTarget.toJvmTarget.target
      }

      compileOptions {
        targetCompatibility = extension.jvmTargetWithDefault
        sourceCompatibility = extension.jvmTargetWithDefault
      }
    }
  }

  private fun setupExtension(target: Project) {
    target.extensions.create<AndroidBasicConventionsExtension>("androidBasicConventions")
  }
}

private val AndroidBasicConventionsExtension.jvmTargetWithDefault
  get() = jvmTarget.convention(JavaVersion.VERSION_1_8).get()

private val JavaVersion.toJvmTarget: JvmTarget
  get() = if (isJava8) JvmTarget.fromTarget("1.8") else JvmTarget.fromTarget(majorVersion)
