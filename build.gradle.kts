import org.gradle.util.GradleVersion
import java.time.Instant

plugins {
  id("fabric-loom") version "0.8.18"
  id("net.nemerosa.versioning") version "be24b23"
  id("signing")
}

group = "dev.sapphic"
version = "1.3.1+1.16"

java {
  withSourcesJar()
}

loom {
  accessWidener = file(".accesswidener")
  refmapName = "mixins/beaconoverhaul/refmap.json"
}

repositories {
  maven("https://maven.jamieswhiteshirt.com/libs-release") {
    content {
      includeGroup("com.jamieswhiteshirt")
    }
  }
}

dependencies {
  minecraft("com.mojang:minecraft:1.16.5")
  mappings(loom.officialMojangMappings())
  modImplementation("net.fabricmc:fabric-loader:0.11.6")
  implementation("org.jetbrains:annotations:21.0.1")
  implementation("org.checkerframework:checker-qual:3.15.0")
  modImplementation(include("net.fabricmc.fabric-api:fabric-resource-loader-v0:0.4.2+ca58154a7d")!!)
  modImplementation(include("com.jamieswhiteshirt:reach-entity-attributes:1.1.1")!!)
  modRuntime("io.github.prospector:modmenu:1.14.13+build.22")
}

tasks {
  compileJava {
    with(options) {
      release.set(8)
      isFork = true
      isDeprecation = true
      encoding = "UTF-8"
      compilerArgs.addAll(listOf("-Xlint:all", "-parameters"))
    }
  }

  processResources {
    filesMatching("/fabric.mod.json") {
      expand("version" to project.version)
    }
  }

  jar {
    from("/LICENSE")

    manifest.attributes(
      "Build-Timestamp" to Instant.now(),
      "Build-Revision" to versioning.info.commit,
      "Build-Jvm" to "${
        System.getProperty("java.version")
      } (${
        System.getProperty("java.vendor")
      } ${
        System.getProperty("java.vm.version")
      })",
      "Built-By" to GradleVersion.current(),

      "Implementation-Title" to project.name,
      "Implementation-Version" to project.version,
      "Implementation-Vendor" to project.group,

      "Specification-Title" to "FabricMod",
      "Specification-Version" to "1.0.0",
      "Specification-Vendor" to project.group,

      "Sealed" to "true"
    )
  }

  assemble {
    dependsOn(versionFile)
  }

  afterEvaluate {
    remapJar {
      remapAccessWidener.set(false)
    }
  }
}

if (hasProperty("signing.mods.keyalias")) {
  val alias = property("signing.mods.keyalias")
  val keystore = property("signing.mods.keystore")
  val password = property("signing.mods.password")

  listOf(tasks.remapJar, tasks.remapSourcesJar).forEach {
    it.get().doLast {
      val file = outputs.files.singleFile
      ant.invokeMethod(
        "signjar", mapOf(
          "jar" to file,
          "alias" to alias,
          "storepass" to password,
          "keystore" to keystore,
          "verbose" to true,
          "preservelastmodified" to true
        )
      )
      signing.sign(file)
    }
  }
}
