plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "cn.timelessmc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        url = uri("https://repo.opencollab.dev/main/")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/public/")
    }
    maven{
        url =uri("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    compileOnly("org.geysermc.floodgate:api:2.2.2-SNAPSHOT")
    implementation("org.jetbrains:annotations:24.0.0")
    implementation( "io.github.biezhi:TinyPinyin:2.0.3.RELEASE")
    implementation("com.github.johnrengelman.shadow:com.github.johnrengelman.shadow.gradle.plugin:7.1.2")


}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    // options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing"))
}

tasks.shadowJar {
    archiveFileName.set("tlmc-tp-spigot-shadow.jar")
}