@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.vanniktech.mavenPublish)
  alias(libs.plugins.dokka)
}

group = "com.infiniteretry.cwackers"
version = "1.0.0"

kotlin {
  androidTarget {
    publishLibraryVariants("release")
  }

  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64(),
    macosX64(),
    macosArm64(),
  ).forEach {
    it.binaries.framework {
      baseName = "Cwackers"
      isStatic = true
    }
  }

  jvm()

  js {
    browser()
    nodejs()
    binaries.executable()
    binaries.library()
  }

  wasmJs {
    browser()
    nodejs()
    binaries.executable()
    binaries.library()
  }

  sourceSets {
    commonMain.dependencies {
      api(compose.ui)
    }
  }
}

android {
  namespace = "com.infiniteretry.cwackers"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  defaultConfig {
    minSdk = libs.versions.android.minSdk.get().toInt()
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

mavenPublishing {
  publishToMavenCentral()

  signAllPublications()

  coordinates(group.toString(), "cwackers", version.toString())

  pom {
    name = "Cwackers"
    description = "A Compose Multiplatform library enabling tiling and patterns."
    inceptionYear = "2025"
    url = "https://github.com/Infinite-Retry/Cwackers"
    licenses {
      license {
        name = "The Apache License, Version 2.0"
        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
      }
    }
    developers {
      developer {
        id = "Infinite-Retry"
        name = "Infinite Retry Labs"
        url = "https://infiniteretry.com"
      }
    }
    scm {
      url = "https://github.com/Infinite-Retry/Cwackers"
      connection = "scm:git:git://github.com/Infinite-Retry/Cwackers.git"
      developerConnection = "scm:git:ssh://github.com/Infinite-Retry/Cwackers.git"
    }
  }
}

tasks.withType<JavaCompile>().configureEach {
  sourceCompatibility = JavaVersion.VERSION_17.toString()
  targetCompatibility = JavaVersion.VERSION_17.toString()
}
tasks.withType<KotlinJvmCompile>().configureEach {
  compilerOptions.jvmTarget = JvmTarget.JVM_17
}
