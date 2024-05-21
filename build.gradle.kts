import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    // Kotlin
    java
    kotlin("jvm") version "1.9.22"
    // Needed for Forge+Fabric
    alias(libs.plugins.architectury.plugin)
    alias(libs.plugins.architectury.loom) apply false
    // Version catalog
    // Modmuss' modpublish plugin
    alias(libs.plugins.modpublish) apply false
    // Shadow
    alias(libs.plugins.shadow) apply false
}

architectury {
    minecraft = property("minecraft_version").toString()
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "me.modmuss50.mod-publish-plugin")

    repositories {
        mavenCentral()
        maven {
            name = "ParchmentMC"
            url = uri("https://maven.parchmentmc.org")
        }
    }

    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")

    loom.apply {
        silentMojangMappingsLicense()
    }

    dependencies {
        // "minecraft"(libs.minecraft)
        "minecraft"("com.mojang:minecraft:${property("minecraft_version")}")
        // The following line declares the mojmap mappings, you may use other mappings as well
        "mappings"(loom.layered {
                officialMojangMappings()
                // parchment(variantOf(libs.parchment) { artifactType("zip") })
                parchment("org.parchmentmc.data:parchment-${property("minecraft_version")}:${property("parchment_version")}@zip")
            }
        )
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")

    // archivesBaseName = property("archives_base_name")
    // base { archivesName = property("archives_base_name") }
    // Determine the version (e.g 1.0.0-fabric+1.18.2#320)
    base.archivesName.set(property("archives_base_name").toString())
    val platform = property("loader_platform")
    val buildNumber = System.getenv("GITHUB_RUN_NUMBER") ?: "local-build"

    version = "${property("mod_version")}-${platform}+${property("minecraft_version")}#${buildNumber}"
    group = property("maven_group") ?: "io.github.slimeymc"

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
    kotlin.target.compilations.all {
        kotlinOptions.jvmTarget = "17"
    }

    java {
        withSourcesJar()
    }

    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://thedarkcolour.github.io/KotlinForForge/")
            content {
                includeGroup("thedarkcolour")
            }
        }
        maven {
            name = "Valkyrien Skies Internal"
            url = uri("https://maven.valkyrienskies.org")
            credentials {
                username = property("vs_maven_username").toString()
                password = property("vs_maven_password").toString()
            }
        }
        maven {
            url = uri("https://api.modrinth.com/maven")
        }
        maven {
            url = uri("https://maven.saps.dev/releases")
            content {
                includeGroup("dev.latvian.mods")
            }
        }
        maven {
            name = "ParchmentMC"
            url = uri("https://maven.parchmentmc.org")
        }
        maven {
            name = "tterrag maven"
            url = uri("https://maven.tterrag.com/")
        }
        maven {
            url = uri("https://cursemaven.com")
            content {
                includeGroup("curse.maven")
            }
        }
        maven {
            url = uri("https://jitpack.io")
        }
        maven {
            url = uri("https://modmaven.dev/")
        }
    }
}

tasks.register<DefaultTask>("renameFiles") {
    val wordToReplace = "slimeyplate"
    val replacementWord = rootProject.property("mod_id").toString()

    // Function to check if a filename contains the word to replace
    fun shouldRename(fileName: String) = fileName.contains(wordToReplace, ignoreCase = false)

    doFirst {
        val rootDir = project.projectDir
        rootDir.walk().filter { it.name.toString().contains(wordToReplace, ignoreCase = false) }.forEach { file ->
            val newName = file.name.toString().replace(wordToReplace, replacementWord, ignoreCase = false)
            logger.info("Renamed file: ${file.name} to ${newName}")
            file.renameTo(file.resolveSibling(newName))
        }
    }
}