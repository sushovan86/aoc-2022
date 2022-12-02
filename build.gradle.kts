plugins {
    kotlin("jvm") version "1.7.22"
}

repositories {
    mavenCentral()
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
            resources.srcDirs("resource")
        }
    }

    wrapper {
        gradleVersion = "7.6"
    }
}
