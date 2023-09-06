plugins {
    id("application")
    id("java")
}

group = "ltd.mingcloud"
version = "1.0"

repositories {
    mavenCentral()
}

application{
    mainClass.set("App")
}

dependencies {
    implementation("org.redisson:redisson:3.23.4")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "App"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}