plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "info.mmpa"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.weisj:darklaf-core:3.0.2")
    implementation("com.github.Minecraft-Malware-Prevention-Alliance:concoction:main-SNAPSHOT")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("info.mmpa.utilsdetector.Main")
}
tasks.test {
    useJUnitPlatform()
}