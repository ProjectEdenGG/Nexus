val eden_api_version: String by project

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
    maven { url = uri("https://repo.dmulloy2.net/nexus/repository/public/") }
    maven { url = uri("https://ci.ender.zone/plugin/repository/everything/") }
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
    maven { url = uri("https://repo.citizensnpcs.co/") }
    maven { url = uri("https://repo.md-5.net/content/groups/public/") }
    maven { url = uri("https://github.com/deanveloper/SkullCreator/raw/mvn-repo/") }
    maven { url = uri("https://repo.viaversion.com") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://maven.enginehub.org/repo/") }
    maven { url = uri("https://eldonexus.de/repository/maven-public/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
    paperweightDevBundle("gg.projecteden.parchment", "1.19.2-R0.1-SNAPSHOT")
    compileOnly("gg.projecteden.parchment:parchment-api:1.19.2-R0.1-SNAPSHOT")
    implementation("io.papermc:paperlib:1.0.8-SNAPSHOT")
    implementation("gg.projecteden:eden-common:${eden_api_version}")
    implementation("gg.projecteden:eden-db:${eden_api_version}")
    implementation("gg.projecteden:eden-discord:${eden_api_version}")
    implementation("org.objenesis:objenesis:3.2")
    implementation("org.checkerframework:checker-qual:3.22.1")
    implementation("com.github.ProjectEdenGG:norm:0843afb4e5")
    implementation("mysql:mysql-connector-java:8.0.29")
    implementation("dev.morphia.morphia:core:1.6.2-SNAPSHOT")
    implementation("org.slf4j:slf4j-api:1.8.0-beta4")
    implementation("it.sauronsoftware.cron4j:cron4j:2.2.5")
    implementation("com.github.instagram4j:instagram4j:2.0.7")
    implementation("org.twitter4j:twitter4j-core:4.0.7")
    implementation("com.github.twitch4j:twitch4j:1.10.0")
    implementation("com.google.api-client:google-api-client:1.35.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20220411-1.32.1")
    implementation("net.wesjd:anvilgui:1.5.3-SNAPSHOT")
    implementation("dev.dbassett:skullcreator:3.0.1")
    implementation("com.vdurmont:emoji-java:5.1.1")
    implementation("com.github.mpkorstanje:simmetrics-core:4.1.1")
    compileOnly("gg.projecteden.crates:api:1.0.4-SNAPSHOT")
    compileOnly("fr.moribus:ImageOnMap:4.2.2-EDEN")
    compileOnly("net.citizensnpcs:citizens-main:2.0.28-SNAPSHOT")
    compileOnly("me.lucko:helper:5.6.10")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.mcMMO-Dev:mcMMO:dc94fedee1")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.griefcraft.lwc:LWCX:2.2.0")
    compileOnly("com.sk89q.worldguard:worldguard-core:7.0.6-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.6-SNAPSHOT")
    compileOnly("com.onarandombox.multiversecore:Multiverse-Core:3.0.0")
    compileOnly("com.onarandombox.multiverseinventories:Multiverse-Inventories:3.0.0")
    compileOnly("com.dumptruckman.minecraft:JsonConfiguration:1.1")
    compileOnly("net.minidev:json-smart:2.4.8")
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.10.0")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("me.filoghost.holographicdisplays:holographicdisplays-api:3.0.0-SNAPSHOT")
    compileOnly("com.github.koca2000:NoteBlockAPI:1.4.4")
    compileOnly("com.github.BlueMap-Minecraft:BlueMapAPI:v1.3.1")
    compileOnly("LibsDisguises:LibsDisguises:10.0.28") {
        exclude("org.spigotmc", "spigot-api")
        exclude("org.spigotmc", "spigot")
    }
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.1")
    compileOnly("com.viaversion:viaversion-api:4.0.1")
    compileOnly("me.clip:placeholderapi:2.10.10")
    compileOnly("com.github.jikoo.OpenInv:openinvapi:4.1.8")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.2.0")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.2.0") { isTransitive = false }
    compileOnly("world.bentobox:bentobox:1.20.1-SNAPSHOT")
    compileOnly("nl.pim16aap2", "BigDoors", "0.1.8.39")
    compileOnly("me.lucko:spark-api:0.1-SNAPSHOT")
    compileOnly(files("libs/BuycraftX.jar"))
    compileOnly(files("libs/GlowAPI.jar"))
    compileOnly(files("libs/nuvotifier-universal-2.3.4.jar"))
    compileOnly(files("libs/SuperVanish-6.2.6.jar"))
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
}