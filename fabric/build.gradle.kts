plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.modpublish)
}

architectury.fabric()

loom {
    accessWidenerPath = project(":common").loom.accessWidenerPath
}

/**
 * @see: https://docs.gradle.org/current/userguide/migrating_from_groovy_to_kotlin_dsl.html
 * */
val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating // Don't use shadow from the shadow plugin because we don't want IDEA to index this.
val developmentFabric: Configuration = configurations.getByName("developmentFabric")
configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    developmentFabric.extendsFrom(configurations["common"])
}

repositories {
    // Do normal insane thing
}

dependencies {
    modImplementation(libs.fabric)
    modApi(libs.fabric.api)
    modImplementation(libs.fabric.kotlin)

    // Architectury API
    modApi(libs.fabric.architectury)

    modImplementation(libs.mixinextras)

    // Valkyrien Skies 2
    modApi(libs.fabric.valkyrienskies) //{
//        exclude(group: "com.simibubi.create")
//    }

    modRuntimeOnly(libs.fabric.chunkloader)
    modRuntimeOnly("maven.modrinth:supermartijn642s-config-lib:1.1.8a-fabric-mc1.18")
    modRuntimeOnly("maven.modrinth:supermartijn642s-core-lib:1.1.17-fabric-mc1.18")

    common(project(":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", configuration = "transformProductionFabric")) { isTransitive = false }
}

tasks {
    processResources {
        filesMatching("fabric.mod.json") {
            expand(project.properties)
        }
    }

    shadowJar {
        exclude("architectury.common.json")
        configurations = listOf(project.configurations["shadowCommon"])
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        dependsOn(shadowJar)
        archiveClassifier.set("fabric")
    }

    jar {
        archiveClassifier.set("dev")
    }

    sourcesJar {
        val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.archiveFile.map { zipTree(it) })
    }

    publishing {
        publishMods {
            file.set(remapJar.get().archiveFile)
            modLoaders.add("fabric")
        }
    }
}