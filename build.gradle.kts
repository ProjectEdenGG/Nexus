@file:Suppress("SpellCheckingInspection")

val edenApiVersion: String by project

plugins {
    `java-library`
    `maven-publish`
    id("io.freefair.lombok") version "6.5.0-rc1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.userdev") version "1.3.7"
}

repositories {
    mavenLocal { content { includeGroup("gg.projecteden") } }
    mavenCentral()
    maven { url = uri("https://sonatype.projecteden.gg/repository/maven-public/") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://repo.onarandombox.com/content/groups/public/") }
    maven { url = uri("https://github.com/deanveloper/SkullCreator/raw/mvn-repo/") }
    maven { url = uri("https://repo.aikar.co/content/groups/aikar/") }
    maven {
        url = uri("https://ci.athion.net/job/FastAsyncWorldEdit/ws/mvn/")
        content { includeGroup("com.fastasyncworldedit") }
    }
    maven {
        url = uri("https://repo.inventivetalent.org/content/groups/public/")
        content { includeGroup("org.inventivetalent") }
    }
    maven { url = uri("https://repo.codemc.io/repository/maven-public/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
    maven { url = uri("https://maven.citizensnpcs.co/repo") }
    maven { url = uri("https://repo.md-5.net/content/groups/public/") }
    maven { url = uri("https://github.com/deanveloper/SkullCreator/raw/mvn-repo/") }
    maven { url = uri("https://repo.viaversion.com") }
    maven { url = uri("https://maven.enginehub.org/repo/") }
    maven { url = uri("https://eldonexus.de/repository/maven-public/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
//    maven { url = uri("https://ci.ender.zone/plugin/repository/everything/") }
//    maven { url = uri("https://ci.ender.zone/plugin/repository/project/") }
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://repo.repsy.io/mvn/quantiom/minecraft") }
}

dependencies {
    paperweightDevBundle("gg.projecteden.parchment", "1.19.4-R0.1-SNAPSHOT")
    compileOnly("gg.projecteden.parchment:parchment-api:1.19.4-R0.1-SNAPSHOT")
    implementation("io.papermc:paperlib:1.0.8-SNAPSHOT")
    implementation("gg.projecteden:eden-common:${edenApiVersion}")
    implementation("gg.projecteden:eden-db:${edenApiVersion}")
    implementation("gg.projecteden:eden-discord:${edenApiVersion}")
    implementation("org.objenesis:objenesis:3.2")
    implementation("org.checkerframework:checker-qual:3.32.0")
    implementation("com.github.ProjectEdenGG:norm:0843afb4e5")
    implementation("mysql:mysql-connector-java:8.0.32")
    implementation("dev.morphia.morphia:core:1.6.2-SNAPSHOT")
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("it.sauronsoftware.cron4j:cron4j:2.2.5")
    implementation("com.github.instagram4j:instagram4j:2.0.7")
    implementation("org.twitter4j:twitter4j-core:4.1.2")
    implementation("com.github.twitch4j:twitch4j:1.14.0")
    implementation("com.google.api-client:google-api-client:2.2.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20221216-2.0.0")
    implementation("dev.dbassett:skullcreator:3.0.1")
    implementation("com.github.mpkorstanje:simmetrics-core:4.1.1")
    implementation("fr.mrmicky:fastboard:1.2.1")
    compileOnly("gg.projecteden.crates:api:1.0.6-SNAPSHOT")
    compileOnly("fr.moribus:ImageOnMap:4.2.2-EDEN")
    compileOnly("me.lucko:helper:5.6.13")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.mcMMO-Dev:mcMMO:dc94fedee1")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.sk89q.worldguard:worldguard-core:7.0.6-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.6-SNAPSHOT")
    compileOnly("com.onarandombox.multiversecore:Multiverse-Core:3.0.0")
    compileOnly("com.onarandombox.multiverseinventories:Multiverse-Inventories:3.0.0")
    compileOnly("com.dumptruckman.minecraft:JsonConfiguration:1.1")
    compileOnly("net.minidev:json-smart:2.4.9")
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.10.0")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("me.filoghost.holographicdisplays:holographicdisplays-api:3.0.0-SNAPSHOT")
    compileOnly("com.github.koca2000:NoteBlockAPI:1.4.4")
    compileOnly("com.github.BlueMap-Minecraft:BlueMapAPI:v1.3.1")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.1")
    compileOnly("com.viaversion:viaversion-api:4.0.1")
    compileOnly("com.github.jikoo.OpenInv:openinvapi:4.1.8")
    compileOnly("world.bentobox:bentobox:1.20.1-SNAPSHOT")
    compileOnly("nl.pim16aap2:BigDoors:0.1.8.39")
    compileOnly("me.lucko:spark-api:0.1-SNAPSHOT")
    compileOnly("com.magmaguy:BetterStructures:1.4.1-SNAPSHOT")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.5.2")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.5.2") { isTransitive = false }
    compileOnly("net.citizensnpcs:citizens-main:2.0.31-SNAPSHOT") {
        exclude("*", "*")
    }
    compileOnly("LibsDisguises:LibsDisguises:10.0.31") {
        exclude("org.spigotmc", "spigot-api")
        exclude("org.spigotmc", "spigot")
    }
    compileOnly(files("libs/BuycraftX.jar"))
    compileOnly(files("libs/GlowAPI.jar"))
    compileOnly(files("libs/LWCX.jar"))
    compileOnly(files("libs/nuvotifier-universal-2.3.4.jar"))
}

group = "gg.projecteden"
description = "Nexus"
version = "2.0.0"

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
        options.compilerArgs.add("-parameters")
    }

    javadoc { options.encoding = Charsets.UTF_8.name() }

    processResources {
        filteringCharset = Charsets.UTF_8.name()

        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        exclude("**/*.png")
        exclude("**/*.txt")
        exclude("lombok/**")
        exclude("**/*.lombok")
        exclude("META-INF/**")
        exclude("kotlin/**")

        relocate("fr.mrmicky.fastboard", "gg.projecteden.nexus.fastboard")

//        minimize {
//            // Force include dependencies
//            exclude("com.github.benmanes:caffeine:.*")
//        }
    }
}