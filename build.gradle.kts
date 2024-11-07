@file:Suppress("UnstableApiUsage")

import info.solidsoft.gradle.pitest.PitestPluginExtension

plugins {
  kotlin("jvm") version "2.0.21"
  id("info.solidsoft.pitest") version "1.15.0"
  id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

testing {
  suites {
    val integrationTest by registering(JvmTestSuite::class) {
      testType = TestSuiteType.INTEGRATION_TEST
      dependencies {
        implementation(project())
        implementation(platform("org.junit:junit-bom:5.9.3"))
        runtimeOnly("org.junit.jupiter:junit-jupiter-engine")
        implementation("org.junit.jupiter:junit-jupiter-api")
        implementation("org.junit.jupiter:junit-jupiter-params")

        implementation("com.willowtreeapps.assertk:assertk-jvm:0.28.1")
      }
      sources {
        kotlin {
          setSrcDirs(listOf("src/integrationTest/kotlin"))
        }
      }
    }
  }
}

configure<PitestPluginExtension> {
  junit5PluginVersion.set("1.0.0")
  avoidCallsTo.set(setOf("kotlin.jvm.internal"))
  mutators.set(setOf("STRONGER"))
  targetClasses.set(setOf("org.rogervinas.*"))
  targetTests.set(setOf("org.rogervinas.*"))
  threads.set(Runtime.getRuntime().availableProcessors())
  outputFormats.set(setOf("XML", "HTML"))
  mutationThreshold.set(75)
  coverageThreshold.set(60)
}

tasks.named("check") {
  dependsOn(":pitest")
}
