plugins {
    `java-library`
    `maven-publish`
    id("io.freefair.lombok") version "6.3.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.userdev") version "1.3.3"
}

repositories {
    maven { url = uri("https://sonatype.projecteden.gg/repository/maven-public/") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://repo.onarandombox.com/content/groups/public/") }
    maven { url = uri("https://ci.ender.zone/plugin/repository/everything/") }
    maven { url = uri("https://maven.sk89q.com/repo/") }
    maven { url = uri("https://ci.athion.net/job/FastAsyncWorldEdit/ws/mvn/") }
    maven { url = uri("https://repo.aikar.co/content/groups/aikar/") }
    maven { url = uri("https://repo.inventivetalent.org/content/groups/public/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-public/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
    maven { url = uri("https://repo.citizensnpcs.co/") }
    maven { url = uri("https://repo.dmulloy2.net/nexus/repository/public/") }
    maven { url = uri("https://repo.md-5.net/content/groups/public/") }
    maven { url = uri("https://github.com/deanveloper/SkullCreator/raw/mvn-repo/") }
    maven { url = uri("https://mvn.intellectualsites.com/content/repositories/thirdparty/") }
    maven { url = uri("https://repo.viaversion.com") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://repo.maven.apache.org/maven2/") }
}

dependencies {
    paperweightDevBundle("me.lexikiq.parchment", "1.18.1-R0.1-SNAPSHOT")
    implementation("io.papermc:paperlib:1.0.2")
    implementation("gg.projecteden:eden-api:1.7.6")
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.objenesis:objenesis:3.2")
    implementation("org.checkerframework:checker-qual:3.20.0")
    implementation("com.github.ProjectEdenGG:norm:0843afb4e5")
    implementation("mysql:mysql-connector-java:8.0.25")
    implementation("dev.morphia.morphia:core:1.6.1")
    implementation("it.sauronsoftware.cron4j:cron4j:2.2.5")
    implementation("com.github.JDA-Applications.JDA-Utilities:jda-utilities-command:804d58a5ed")
    implementation("org.twitter4j:twitter4j-core:4.0.7")
    implementation("com.github.twitch4j:twitch4j:1.6.0")
    implementation("com.google.api-client:google-api-client:1.32.2")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.32.1")
    implementation("com.google.apis:google-api-services-sheets:v4-rev612-1.25.0")
    implementation("com.comphenix.packetwrapper:PacketWrapper:1.15.2-R0.1-SNAPSHOT")
    implementation("net.wesjd:anvilgui:1.5.1-SNAPSHOT")
    implementation("dev.dbassett:skullcreator:3.0.1")
    implementation("org.inventivetalent:boundingboxapi:1.4.1-SNAPSHOT")
    compileOnly("me.lucko:helper:5.6.8")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.mcMMO-Dev:mcMMO:dc94fedee1")
    compileOnly("net.luckperms:api:5.3")
    compileOnly("com.griefcraft.lwc:LWCX:2.2.0")
    compileOnly("com.sk89q.worldguard:worldguard-core:7.0.6-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.6-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.0-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-core:7.2.0-SNAPSHOT")
    compileOnly("com.onarandombox.multiversecore:Multiverse-Core:3.0.0")
    compileOnly("com.onarandombox.multiverseinventories:Multiverse-Inventories:3.0.0")
    compileOnly("com.dumptruckman.minecraft:JsonConfiguration:1.1")
    compileOnly("net.minidev:json-smart:2.4.7")
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.7.1")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.1-SNAPSHOT")
    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.0")
    compileOnly("net.citizensnpcs:citizens-main:2.0.28-SNAPSHOT")
    compileOnly("com.github.koca2000:NoteBlockAPI:1.4.4")
    compileOnly("com.github.BlueMap-Minecraft:BlueMapAPI:v1.3.1")
    compileOnly("LibsDisguises:LibsDisguises:10.0.24")
    compileOnly("me.arcaniax:HeadDatabase-API:1.1.0")
    compileOnly("com.viaversion:viaversion-api:4.0.1")
    compileOnly("me.clip:placeholderapi:2.10.10")
    compileOnly("com.github.jikoo.OpenInv:openinvapi:4.1.8")
    compileOnly(files("libs/BuycraftX.jar"))
    compileOnly(files("libs/FastAsyncWorldEdit-1.18-44.jar"))
    compileOnly(files("libs/GlowAPI.jar"))
    compileOnly(files("libs/nuvotifier-universal-2.3.4.jar"))
    compileOnly(files("libs/SmartInvs-1.3.4.jar"))
    compileOnly(files("libs/SuperVanish-6.2.6.jar"))
}

group = "gg.projecteden"
description = "Nexus"
version = "2.0"
java.sourceCompatibility = JavaVersion.VERSION_16

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
    processResources { filteringCharset = Charsets.UTF_8.name() }
}
