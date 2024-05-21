buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        // Make sure this version matches the one included in Kotlin for Forge
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        // OPTIONAL Gradle plugin for Kotlin Serialization
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.6.10")
    }
}

plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.modpublish)
//    id "com.matthewprenger.cursegradle"
//    id "com.modrinth.minotaur"
}

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    accessWidenerPath = project(":common").loom.accessWidenerPath

    forge {
        convertAccessWideners.set(true)
        mixinConfig("slimeyplate.mixins.json")
        mixinConfig("slimeyplate-common.mixins.json")
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
    }
}

/**
 * @see: https://docs.gradle.org/current/userguide/migrating_from_groovy_to_kotlin_dsl.html
 * */
val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating // Don't use shadow from the shadow plugin because we don't want IDEA to index this.
val developmentForge: Configuration = configurations.getByName("developmentForge")
configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    developmentForge.extendsFrom(configurations["common"])
}

repositories {
    // This is too insane
}

dependencies {
    forge(libs.forge)

    // Architectury API
    modApi(libs.forge.architectury)

    // Valkyrien Skies 2
    modApi(libs.forge.valkyrienskies) { isTransitive = false }

    common(project(":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", configuration = "transformProductionForge")) { isTransitive = false }

    // Kotlin for Forge
    implementation(libs.forge.kotlin)

    modCompileOnly(libs.forge.chunkloader)
}

tasks {
    processResources {
        inputs.property("version", project.version)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE


        filesMatching("META-INF/mods.toml") {
            expand(project.properties)
        }
    }

    shadowJar {
        exclude("fabric.mod.json")
        exclude("architectury.common.json")
        configurations = listOf(project.configurations["shadowCommon"])
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        dependsOn(shadowJar)
        archiveClassifier.set("forge")
    }

    jar {
        archiveClassifier.set("dev")
    }

    sourcesJar {
        val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.archiveFile.map { zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    publishing {
        publishMods {
            file.set(jar.get().archiveFile)
            modLoaders.add("forge")
        }
    }
}