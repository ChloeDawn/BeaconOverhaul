import java.nio.file.Files
import java.nio.file.Paths

plugins {
  id("fabric-loom") version "0.5.43"
  id("signing")
}

group = "dev.sapphic"
version = "1.0.0"

minecraft {
  //accessWidener = file(".accesswidener")
  refmapName = "mixins/beacons/refmap.json"
  runDir = "run"
}

repositories {
  maven("https://maven.jamieswhiteshirt.com/libs-release") {
    content {
      includeGroup("com.jamieswhiteshirt")
    }
  }
}

dependencies {
  minecraft("com.mojang:minecraft:20w49a")
  mappings(minecraft.officialMojangMappings())
  modImplementation("net.fabricmc:fabric-loader:0.10.8")
  implementation("org.jetbrains:annotations:20.1.0")
  implementation("org.checkerframework:checker-qual:3.8.0")
  modImplementation(include(fabricApi.module("fabric-resource-loader-v0", "0.28.1+1.17"))!!)
  modImplementation(include("com.jamieswhiteshirt:reach-entity-attributes:1.0.1")!!)
}

tasks {
  compileJava {
    with(options) {
      options.release.set(8)
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
    manifest.attributes(mapOf(
      "Specification-Title" to "MinecraftMod",
      "Specification-Vendor" to project.group,
      "Specification-Version" to "1.0.0",
      "Implementation-Title" to project.name,
      "Implementation-Version" to project.version,
      "Implementation-Vendor" to project.group,
      "Sealed" to "true"
    ))
  }

  create<Jar>("signJar") {
    dependsOn("jar")

    val alias = project.property("signing.mods.keyalias")
    val keystore = project.property("signing.mods.keystore")
    val password = project.property("signing.mods.password")

    doLast {
      val libs = Files.createDirectories(Paths.get("$buildDir/libs")).toAbsolutePath()
      Files.newDirectoryStream(libs, "*.jar").forEach { artifact ->
        ant.invokeMethod("signjar", mapOf(
          "destDir" to libs,
          "jar" to artifact,
          "alias" to alias,
          "storepass" to password,
          "keystore" to keystore,
          "storetype" to "jks"
        ))
        signing.sign(artifact.toFile())
      }
    }
  }

  assemble {
    dependsOn("signJar")
  }
}
