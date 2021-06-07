import org.gradle.util.GradleVersion
import java.time.Instant

plugins {
  id("fabric-loom") version "0.8.17"
  id("net.nemerosa.versioning") version "be24b23"
  id("signing")
}

group = "dev.sapphic"
version = "1.4.0+1.17"

java {
  withSourcesJar()
}

loom {
  accessWidener = file(".accesswidener")
  refmapName = "mixins/beaconoverhaul/refmap.json"
  runs {
    configureEach {
      vmArg("-Dmixin.debug=true")
      vmArg("-Dmixin.debug.export.decompile=false")
      vmArg("-Dmixin.debug.verbose=true")
      vmArg("-Dmixin.dumpTargetOnFailure=true")
      vmArg("-Dmixin.checks=true")
      vmArg("-Dmixin.hotSwap=true")
    }
  }
}

repositories {
  mavenLocal()
  maven("https://maven.jamieswhiteshirt.com/libs-release") {
    content {
      includeGroup("com.jamieswhiteshirt")
    }
  }
  maven("https://maven.terraformersmc.com/releases") {
    content {
      includeGroup("com.terraformersmc")
    }
  }
}

dependencies {
  minecraft("com.mojang:minecraft:1.17-pre1")
  mappings(loom.officialMojangMappings())
  modImplementation("net.fabricmc:fabric-loader:0.11.3")
  implementation("org.jetbrains:annotations:21.0.1")
  implementation("org.checkerframework:checker-qual:3.14.0")
  modImplementation(include(fabricApi.module("fabric-resource-loader-v0", "0.34.9+1.17"))!!)
  modImplementation(include("com.jamieswhiteshirt:reach-entity-attributes:2.1.1")!!)
  modRuntime("com.terraformersmc:modmenu:2.0.0-beta.7")
}

tasks {
  compileJava {
    with(options) {
      options.release.set(16)
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
