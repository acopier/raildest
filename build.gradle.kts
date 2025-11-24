import io.papermc.hangarpublishplugin.model.Platforms

plugins {
  kotlin("jvm") version "2.3.0-RC"
  id("xyz.jpenilla.run-paper") version "3.0.2"
  id("com.gradleup.shadow") version "9.2.2"
  id("de.eldoria.plugin-yml.bukkit") version "0.8.0"
  id("io.papermc.hangar-publish-plugin") version "0.1.3"
}

group = "io.github.acopier"
version = "2.0"
val mcVersion = "1.21"

repositories {
  mavenCentral()
  maven {
    name = "papermc-repo"
    url = uri("https://repo.papermc.io/repository/maven-public/")
  }
}

dependencies {
  compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
  implementation("com.cjcrafter:foliascheduler:0.7.2")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks {
  runServer {
    // Configure the Minecraft version for our task.
    // This is the only required configuration besides applying the plugin.
    // Your plugin"s jar (or shadowJar if present) will be used automatically.
    minecraftVersion("1.21.8")
  }
}

val targetJavaVersion = 21
kotlin {
  jvmToolchain(targetJavaVersion)
}

bukkit {
  name = "RailDest"
  description = "A plugin that allows fully automatic rail junctions."
  website = "https://github.com/acopier/raildest"
  author = "acopier"
  main = "io.github.acopier.raildest.RailDestPlugin"
  foliaSupported = true
  apiVersion = mcVersion
}

tasks.shadowJar {
  archiveFileName.set("RailDest-${version}.jar")
  relocate(
    "com.cjcrafter.foliascheduler",
    "io.github.acopier.raildest.schedulers"
  )
}

tasks.processResources {
  val props = mapOf("version" to version)
  inputs.properties(props)
  filteringCharset = "UTF-8"
  filesMatching("plugin.yml") {
    expand(props)
  }
}

hangarPublish {
  publications.register("plugin") {
    version.set(project.version as String)
    channel.set("Release")
    id.set("RailDest")
    apiKey.set(System.getenv("HANGAR_API_TOKEN"))
    platforms {
      register(Platforms.PAPER) {
        jar.set(tasks.shadowJar.flatMap { it.archiveFile })
        val versions: List<String> = "$mcVersion.x"
          .split(",")
          .map { it.trim() }
        platformVersions.set(versions)
      }
    }
  }
}

tasks.build {
  dependsOn("shadowJar")
}