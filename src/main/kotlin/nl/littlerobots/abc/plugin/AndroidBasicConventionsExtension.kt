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

import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

interface AndroidBasicConventionsExtension {
  val minSdk: Property<Int>
  val targetSdk: Property<Int>
  val compileSdk: Property<Int>
  val jvmTarget: Property<JavaVersion>
  @get:Nested val kotlinOptions: AndroidBasicConventionsKotlinOptions

  fun kotlinOptions(action: Action<AndroidBasicConventionsKotlinOptions>) {
    action.execute(kotlinOptions)
  }
}

abstract class AndroidBasicConventionsKotlinOptions {
  open var freeCompilerArgs: List<String> = emptyList()
}